// backend/gcv-service/src/main/java/com/pistore/gcv/GCVDTO.java
package com.pistore.gcv;

public class GCVDTO {
    private String currency;
    private double rate;

    public GCVDTO() {}
    public GCVDTO(String currency, double rate) {
        this.currency = currency;
        this.rate = rate;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
}
