// backend/rate-service/src/main/java/com/pistore/rate/RateRecord.java
package com.pistore.rate;

public class RateRecord {
    private String currency;
    private double rate;
    private long timestamp;

    public RateRecord() {}
    public RateRecord(String currency, double rate, long timestamp) {
        this.currency = currency;
        this.rate = rate;
        this.timestamp = timestamp;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
