// backend/rate-service/src/main/java/com/pistore/rate/ExchangeRateClient.java
package com.pistore.rate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ExchangeRateClient {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateClient.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ChainlinkOracleClient oracleClient; // Injeksi oracle dari implementasi sebelumnya

    @Value("${exchange.list:okx,bitget,pionex,mexc,gateio}")
    private String[] exchanges;
    @Value("${okx.api.key}") private String okxApiKey;
    @Value("${okx.api.secret}") private String okxApiSecret;
    @Value("${bitget.api.key}") private String bitgetApiKey;
    @Value("${bitget.api.secret}") private String bitgetApiSecret;
    @Value("${pionex.api.key}") private String pionexApiKey;
    @Value("${pionex.api.secret}") private String pionexApiSecret;
    @Value("${mexc.api.key}") private String mexcApiKey;
    @Value("${mexc.api.secret}") private String mexcApiSecret;
    @Value("${gateio.api.key}") private String gateioApiKey;
    @Value("${gateio.api.secret}") private String gateioApiSecret;

    private static final String OKX_API = "https://www.okx.com/api/v5/market/ticker?instId=PI-USDT";
    private static final String BITGET_API = "https://api.bitget.com/api/v2/spot/market/ticker?symbol=PIUSDT";
    private static final String PIONEX_API = "https://api.pionex.com/api/v1/market/tickers?symbol=PI_USDT";
    private static final String MEXC_API = "https://api.mexc.com/api/v3/ticker/price?symbol=PIUSDT";
    private static final String GATEIO_API = "https://api.gateio.ws/api/v4/spot/tickers?currency_pair=PI_USDT";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    private static final long CACHE_TTL_SECONDS = 300; // 5 menit

    @PostConstruct
    public void init() {
        logger.info("Menginisialisasi ExchangeRateClient dengan bursa: {}", Arrays.toString(exchanges));
    }

    public double getAveragePiRate() {
        String cacheKey = "pi:external:USD";
        String cachedRate = redisTemplate.opsForValue().get(cacheKey);
        if (cachedRate != null) {
            logger.debug("Mengambil harga dari cache: {}", cachedRate);
            return Double.parseDouble(cachedRate);
        }

        double totalRate = 0.0;
        int validExchanges = 0;

        for (String exchange : exchanges) {
            try {
                double rate = getRateFromExchange(exchange);
                totalRate += rate;
                validExchanges++;
                logger.info("Harga dari {}: ${}", exchange, rate);
            } catch (Exception e) {
                logger.error("Gagal mengambil harga dari {}: {}", exchange, e.getMessage());
            }
        }

        double averageRate;
        if (validExchanges > 0) {
            averageRate = totalRate / validExchanges;
        } else {
            logger.warn("Semua API bursa gagal, mencoba oracle Chainlink");
            try {
                averageRate = oracleClient.getPiPriceFromOracle();
                logger.info("Harga dari Chainlink Oracle: ${}", averageRate);
            } catch (Exception e) {
                logger.error("Oracle gagal, menggunakan fallback: $0.8111");
                averageRate = 0.8111; // Fallback terakhir (OKX, Mei 2025)
            }
        }

        redisTemplate.opsForValue().set(cacheKey, String.valueOf(averageRate), CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        logger.debug("Menyimpan harga ke cache: ${}", averageRate);
        return averageRate;
    }

    private double getRateFromExchange(String exchange) {
        String url;
        String apiKey;
        String apiSecret;
        switch (exchange.toLowerCase()) {
            case "okx":
                url = OKX_API;
                apiKey = okxApiKey;
                apiSecret = okxApiSecret;
                break;
            case "bitget":
                url = BITGET_API;
                apiKey = bitgetApiKey;
                apiSecret = bitgetApiSecret;
                break;
            case "pionex":
                url = PIONEX_API;
                apiKey = pionexApiKey;
                apiSecret = pionexApiSecret;
                break;
            case "mexc":
                url = MEXC_API;
                apiKey = mexcApiKey;
                apiSecret = mexcApiSecret;
                break;
            case "gateio":
                url = GATEIO_API;
                apiKey = gateioApiKey;
                apiSecret = gateioApiSecret;
                break;
            default:
                throw new IllegalArgumentException("Bursa tidak didukung: " + exchange);
        }

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String response = makeApiCall(url, apiKey, apiSecret);
                return parseResponse(exchange, response);
            } catch (RestClientException e) {
                logger.warn("Percobaan {} gagal untuk {}: {}", attempt, exchange, e.getMessage());
                if (attempt == MAX_RETRIES) {
                    throw e;
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry dihentikan", ie);
                }
            }
        }
        throw new RuntimeException("Gagal mengambil harga dari " + exchange + " setelah " + MAX_RETRIES + " percobaan");
    }

    private String makeApiCall(String url, String apiKey, String apiSecret) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.set("OK-ACCESS-KEY", apiKey); // Contoh untuk OKX, sesuaikan untuk bursa lain
            // Tambahkan logika autentikasi spesifik (misalnya, HMAC untuk Bitget/MEXC)
            // Contoh sederhana, ganti dengan implementasi autentikasi sebenarnya
            headers.set("OK-ACCESS-SIGN", generateSignature(apiSecret, url));
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    private String generateSignature(String apiSecret, String url) {
        // Placeholder: Implementasi HMAC-SHA256 untuk autentikasi
        // Ganti dengan logika spesifik per bursa (misalnya, timestamp + signature)
        return "dummy-signature"; // Ganti dengan HMAC-SHA256 sebenarnya
    }

    private double parseResponse(String exchange, String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            switch (exchange.toLowerCase()) {
                case "okx":
                    return parseOkxResponse(root);
                case "bitget":
                    return parseBitgetResponse(root);
                case "pionex":
                    return parsePionexResponse(root);
                case "mexc":
                    return parseMexcResponse(root);
                case "gateio":
                    return parseGateioResponse(root);
                default:
                    throw new IllegalArgumentException("Bursa tidak didukung: " + exchange);
            }
        } catch (Exception e) {
            logger.error("Gagal parsing respons dari {}: {}", exchange, e.getMessage());
            return getFallbackRate(exchange);
        }
    }

    private double parseOkxResponse(JsonNode root) {
        JsonNode data = root.path("data").get(0);
        if (data != null && data.has("last")) {
            return data.get("last").asDouble();
        }
        logger.warn("Respons OKX tidak valid, menggunakan fallback: $0.8111");
        return 0.8111;
    }

    private double parseBitgetResponse(JsonNode root) {
        JsonNode data = root.path("data");
        if (data != null && data.has("close")) {
            return data.get("close").asDouble();
        }
        logger.warn("Respons Bitget tidak valid, menggunakan fallback: $0.7357");
        return 0.7357;
    }

    private double parsePionexResponse(JsonNode root) {
        JsonNode ticker = root.path("data").path("tickers").get(0);
        if (ticker != null && ticker.has("lastPrice")) {
            return ticker.get("lastPrice").asDouble();
        }
        logger.warn("Respons Pionex tidak valid, menggunakan fallback: $0.7801");
        return 0.7801;
    }

    private double parseMexcResponse(JsonNode root) {
        if (root.has("price")) {
            return root.get("price").asDouble();
        }
        logger.warn("Respons MEXC tidak valid, menggunakan fallback: $0.7900");
        return 0.7900;
    }

    private double parseGateioResponse(JsonNode root) {
        JsonNode ticker = root.get(0);
        if (ticker != null && ticker.has("last")) {
            return ticker.get("last").asDouble();
        }
        logger.warn("Respons Gate.io tidak valid, menggunakan fallback: $0.8200");
        return 0.8200;
    }

    private double getFallbackRate(String exchange) {
        switch (exchange.toLowerCase()) {
            case "okx": return 0.8111;
            case "bitget": return 0.7357;
            case "pionex": return 0.7801;
            case "mexc": return 0.7900;
            case "gateio": return 0.8200;
            default: return 0.8111;
        }
    }
    }
