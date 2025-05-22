// backend/smartcontract-service/src/main/java/com/pistore/smartcontract/ConversionService.java
package com.pistore.smartcontract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConversionService {
    @Autowired
    private RestTemplate restTemplate;

    public ConversionDTO convertPiToFiat(String merchantId, double amountInPi, String source) {
        double piRate = source.equals("mining") ? getExternalPiRate() : getExternalPiRate(); // Mining dikonversi ke eksternal
        double amountInUSD = amountInPi * piRate;

        ConversionDTO conversion = new ConversionDTO();
        conversion.setId(generateConversionId());
        conversion.setMerchantId(merchantId);
        conversion.setAmountInPi(amountInPi);
        conversion.setAmountInUSD(amountInUSD);
        conversion.setStatus("pending");
        // Kirim permintaan ke bursa (misalnya, OKX) untuk konversi fiat
        // Placeholder: Integrasi API bursa
        conversion.setStatus("completed");
        return conversion;
    }

    private double getExternalPiRate() {
        String url = "http://rate-service/api/rate/pi?type=external";
        RateDTO rate = restTemplate.getForObject(url, RateDTO.class);
        return rate != null ? rate.getRate() : 0.8111;
    }

    private String generateConversionId() {
        return "CONV-" + System.currentTimeMillis();
    }
}
