package com.pistore.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RestTemplate restTemplate;

    public ProductDTO createProduct(String merchantId, String name, double priceInPi) {
        double externalRate = getExternalPiRate();
        double priceInUSD = priceInPi * externalRate;

        // Validasi: Harga USD harus masuk akal (misalnya, < $10,000 untuk barang umum)
        if (priceInUSD > 10000.0) {
            throw new IllegalArgumentException("Harga barang melebihi batas wajar: $" + priceInUSD);
        }

        ProductDTO product = new ProductDTO();
        product.setId(generateProductId());
        product.setMerchantId(merchantId);
        product.setName(name);
        product.setPriceInPi(priceInPi);
        product.setPriceInUSD(priceInUSD);
        mongoTemplate.save(product, "products");
        return product;
    }

    private double getExternalPiRate() {
        String url = "http://rate-service/api/rate/pi?type=external";
        RateDTO rate = restTemplate.getForObject(url, RateDTO.class);
        return rate != null ? rate.getRate() : 0.8111;
    }

    private String generateProductId() {
        return "PROD-" + System.currentTimeMillis();
    }
}
