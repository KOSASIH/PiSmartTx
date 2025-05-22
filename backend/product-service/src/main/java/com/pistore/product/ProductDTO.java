// backend/product-service/src/main/java/com/pistore/product/ProductDTO.java
package com.pistore.product;

public class ProductDTO {
    private String id;
    private String merchantId;
    private String name;
    private double priceInPi;
    private double priceInUSD;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPriceInPi() { return priceInPi; }
    public void setPriceInPi(double priceInPi) { this.priceInPi = priceInPi; }
    public double getPriceInUSD() { return priceInUSD; }
    public void setPriceInUSD(double priceInUSD) { this.priceInUSD = priceInUSD; }
}
