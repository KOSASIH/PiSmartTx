// backend/smartcontract-service/src/main/java/com/pistore/smartcontract/TransactionDTO.java
package com.pistore.smartcontract;

public class TransactionDTO {
    private String id;
    private String userId;
    private String merchantId;
    private double amount;
    private String status;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
