// backend/smartcontract-service/src/main/java/com/pistore/smartcontract/SmartContractService.java
package com.pistore.smartcontract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class SmartContractService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RestTemplate restTemplate;

    public TransactionDTO createTransaction(String userId, String merchantId, double amountInPi) {
        // Fetch Pi rate
        double piRate = getPiRate();
        double amountInUSD = amountInPi * piRate;

        // Validate transaction
        if (!isValidTransaction(merchantId, amountInUSD)) {
            throw new IllegalArgumentException("Transaction amount invalid based on Pi rate");
        }

        TransactionDTO transaction = new TransactionDTO();
        transaction.setId(generateTransactionId());
        transaction.setUserId(userId);
        transaction.setMerchantId(merchantId);
        transaction.setAmount(amountInPi);
        transaction.setUsdAmount(amountInUSD);
        transaction.setStatus("pending");
        mongoTemplate.save(transaction, "transactions");
        return transaction;
    }

    public TransactionDTO executeTransaction(String transactionId) {
        TransactionDTO transaction = mongoTemplate.findById(transactionId, TransactionDTO.class, "transactions");
        if (transaction != null) {
            transaction.setStatus("completed");
            mongoTemplate.save(transaction, "transactions");
            // TODO: Integrate with Pi Network blockchain when available
        }
        return transaction;
    }

    public List<TransactionDTO> getMerchantTransactions(String merchantId) {
        return mongoTemplate.find(Query.query(Criteria.where("merchantId").is(merchantId)), TransactionDTO.class, "transactions");
    }

    private double getPiRate() {
        String url = "http://rate-service/api/rate/pi";
        RateDTO rate = restTemplate.getForObject(url, RateDTO.class);
        return rate != null ? rate.getRate() : 314159.0; // Fallback rate
    }

    private boolean isValidTransaction(String merchantId, double amountInUSD) {
        // Example: Ensure amount is within acceptable range
        return amountInUSD >= 0.01; // Minimum transaction $0.01
    }

    private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis();
    }
            }
