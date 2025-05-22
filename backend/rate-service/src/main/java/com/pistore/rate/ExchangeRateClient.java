// backend/rate-service/src/main/java/com/pistore/rate/ExchangeRateClient.java
package com.pistore.rate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExchangeRateClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String[] EXCHANGES = {"okx", "bitget", "pionex", "mexc", "gateio"};

    @Value("${okx.api.key}") private String okxApiKey;
    @Value("${bitget.api.key}") private String bitgetApiKey;
    @Value("${pionex.api.key}") private String pionexApiKey;
    @Value("${mexc.api.key}") private String mexcApiKey;
    @Value("${gateio.api.key}") private String gateioApiKey;

    private static final String OKX_API = "https://www.okx.com/api/v5/market/ticker?instId=PI-USDT";
    private static final String BITGET_API = "https://api.bitget.com/api/v2/spot/market/ticker?symbol=PIUSDT";
    private static final String PIONEX_API = "https://api.pionex.com/api/v1/market/tickers?symbol=PI_USDT";
    private static final String MEXC_API = "https://api.mexc.com/api/v3/ticker/price?symbol=PIUSDT";
    private static final String GATEIO_API = "https://api.gateio.ws/api/v4/spot/tickers?currency_pair=PI_USDT";

    public double getAveragePiRate() {
        double totalRate = 0.0;
        int validExchanges = 0;

        for (String exchange : EXCHANGES) {
            try {
                double rate = getRateFromExchange(exchange);
                totalRate += rate;
                validExchanges++;
            } catch (Exception e) {
                System.err.println("Gagal mengambil harga dari " + exchange + ": " + e.getMessage());
            }
        }

        return validExchanges > 0 ? totalRate / validExchanges : 0.8111; // Fallback ke harga OKX, Mei 2025
    }

    private double getRateFromExchange(String exchange) {
        String url;
        String apiKey;
        switch (exchange) {
            case "okx":
                url = OKX_API;
                apiKey = okxApiKey;
                return parseOkxResponse(makeApiCall(url, apiKey));
            case "bitget":
                url = BITGET_API;
                apiKey = bitgetApiKey;
                return parseBitgetResponse(makeApiCall(url, apiKey));
            case "pionex":
                url = PIONEX_API;
                apiKey = pionexApiKey;
                return parsePionexResponse(makeApiCall(url, apiKey));
            case "mexc":
                url = MEXC_API;
                apiKey = mexcApiKey;
                return parseMexcResponse(makeApiCall(url, apiKey));
            case "gateio":
                url = GATEIO_API;
                apiKey = gateioApiKey;
                return parseGateioResponse(makeApiCall(url, apiKey));
            default:
                throw new IllegalArgumentException("Bursa tidak didukung: " + exchange);
        }
    }

    private String makeApiCall(String url, String apiKey) {
        // Tambahkan header API key jika diperlukan
        // Contoh: restTemplate.getForObject(url, String.class, headers dengan apiKey)
        return restTemplate.getForObject(url, String.class); // Sederhana, tambahkan autentikasi jika perlu
    }

    private double parseOkxResponse(String response) {
        // Contoh: {"code":"0","msg":"","data":[{"instId":"PI-USDT","last":"0.8111"}]}
        return response.contains("last") ? Double.parseDouble(response.split("\"last\":\"")[1].split("\"")[0]) : 0.8111;
    }

    private double parseBitgetResponse(String response) {
        // Contoh: {"code":"00000","msg":"success","data":{"symbol":"PIUSDT","close":"0.7357"}}
        return response.contains("close") ? Double.parseDouble(response.split("\"close\":\"")[1].split("\"")[0]) : 0.7357;
    }

    private double parsePionexResponse(String response) {
        // Contoh: {"result":true,"data":{"tickers":[{"symbol":"PI_USDT","lastPrice":"0.7801"}]}}
        return response.contains("lastPrice") ? Double.parseDouble(response.split("\"lastPrice\":\"")[1].split("\"")[0]) : 0.7801;
    }

    private double parseMexcResponse(String response) {
        // Contoh: {"symbol":"PIUSDT","price":"0.7900"}
        return response.contains("price") ? Double.parseDouble(response.split("\"price\":\"")[1].split("\"")[0]) : 0.7900;
    }

    private double parseGateioResponse(String response) {
        // Contoh: [{"currency_pair":"PI_USDT","last":"0.8200"}]
        return response.contains("last") ? Double.parseDouble(response.split("\"last\":\"")[1].split("\"")[0]) : 0.8200;
    }
}
