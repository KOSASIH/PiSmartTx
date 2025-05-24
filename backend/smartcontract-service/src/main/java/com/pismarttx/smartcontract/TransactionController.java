// backend/smartcontract-service/src/main/java/com/pismarttx/smartcontract/TransactionController.java
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private Web3j web3j;
    @Autowired
    private PiStoreContract piStoreContract;

    @GetMapping("/analytics/{userId}")
    public ResponseEntity<TransactionAnalytics> getAnalytics(@PathVariable String userId, @RequestParam String period) {
        try {
            List<Transaction> transactions = piStoreContract.getUserTransactions(userId).send();
            TransactionAnalytics analytics = new TransactionAnalytics();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start;

            switch (period) {
                case "daily":
                    start = now.minusDays(1);
                    break;
                case "weekly":
                    start = now.minusWeeks(1);
                    break;
                case "monthly":
                    start = now.minusMonths(1);
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }

            BigDecimal totalPi = BigDecimal.ZERO;
            BigDecimal totalUSD = BigDecimal.ZERO;
            int internalCount = 0;
            int externalCount = 0;

            for (Transaction tx : transactions) {
                if (tx.getTimestamp().isAfter(start)) {
                    totalPi = totalPi.add(tx.getAmount());
                    BigDecimal rate = tx.getPaymentType().equals("internal") ? new BigDecimal("314159") : getExternalRate();
                    totalUSD = totalUSD.add(tx.getAmount().multiply(rate));
                    if (tx.getPaymentType().equals("internal")) internalCount++;
                    else externalCount++;
                }
            }

            analytics.setTotalPi(totalPi);
            analytics.setTotalUSD(totalUSD);
            analytics.setInternalPercentage((double) internalCount / (internalCount + externalCount) * 100);
            analytics.setExternalPercentage((double) externalCount / (internalCount + externalCount) * 100);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Error fetching analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private BigDecimal getExternalRate() {
        // Ambil dari Redis atau rate-service
        return new BigDecimal("0.8111"); // Fallback
    }
}

public class TransactionAnalytics {
    private BigDecimal totalPi;
    private BigDecimal totalUSD;
    private double internalPercentage;
    private double externalPercentage;
    // Getters dan setters
}
