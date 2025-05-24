// backend/rate-service/src/main/java/com/pismarttx/rate/RateService.java
public double getExternalRate() {
    List<String> exchanges = Arrays.asList("okx", "bitget", "pionex", "mexc", "gateio");
    for (String exchange : exchanges) {
        try {
            double rate = fetchRateFromExchange(exchange);
            if (rate > 0) return rate;
        } catch (Exception e) {
            log.warn("Failed to fetch rate from {}: {}", exchange, e.getMessage());
        }
    }
    return 0.8111; // Fallback rate
}
