// backend/smartcontract-service/src/main/java/com/pistore/smartcontract/SmartContractService.java
package com.pistore.smartcontract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class SmartContractService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public TransactionDTO createTransaction(String userId, String merchantId, double amount) {
        // Simulate smart contract: Store transaction in MongoDB with 'pending' status
        TransactionDTO transaction = new TransactionDTO();
        transaction.setId(generateTransactionId());
        transaction.setUserId(userId);
        transaction.setMerchantId(merchantId);
        transaction.setAmount(amount);
        transaction.setStatus("pending");
        mongoTemplate.save(transaction, "transactions");
        return transaction;
    }

    public TransactionDTO executeTransaction(String transactionId) {
        // Simulate execution: Update transaction status to 'completed'
        TransactionDTO transaction = mongoTemplate.findById(transactionId, TransactionDTO.class, "transactions");
        if (transaction != null) {
            transaction.setStatus("completed");
            mongoTemplate.save(transaction, "transactions");
            // TODO: Integrate with Pi Network blockchain API when available
        }
        return transaction;
    }

    private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis(); // Simple ID generator
    }
}
