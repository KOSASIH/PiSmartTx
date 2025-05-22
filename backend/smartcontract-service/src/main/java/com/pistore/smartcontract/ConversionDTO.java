// backend/smartcontract-service/src/main/java/com/pistore/smartcontract/ConversionDTO.java
package com.pistore.smartcontract;

public class ConversionDTO {
    private String id;
    private String merchantId;
    private double amountInPi;
    private double amountInUSD;
    private String status;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public double getAmountInPi() { return amountInPi; }
    public void setAmountInPi(double amountInPi) { this.amountInPi = amountInPi; }
    public double getAmountInUSD() { return amountInUSD; }
    public void setAmountInUSD(double amountInUSD) { this.amountInUSD = amountInUSD; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
