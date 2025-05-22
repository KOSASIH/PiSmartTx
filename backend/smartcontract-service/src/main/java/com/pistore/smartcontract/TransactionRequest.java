// backend/smartcontract-service/src/main/java/com/pistore/smartcontract/TransactionRequest.java
package com.pistore.smartcontract;

public class TransactionRequest {
    private String userId;
    private String merchantId;
    private double amountInPi;
    private String source;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public double getAmountInPi() { return amountInPi; }
    public void setAmountInPi(double amountInPi) { this.amountInPi = amountInPi; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
