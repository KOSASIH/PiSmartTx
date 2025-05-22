// backend/rate-service/src/main/java/com/pistore/rate/RateDTO.java
package com.pistore.rate;

public class RateDTO {
    private String currency;
    private double rate;

    public RateDTO() {}
    public RateDTO(String currency, double rate) {
        this.currency = currency;
        this.rate = rate;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
}
