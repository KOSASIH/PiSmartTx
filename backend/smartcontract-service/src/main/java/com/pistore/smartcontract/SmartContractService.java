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

    public TransactionDTO createTransaction(String userId, String merchantId, double amountInPi, String source) {
        String rateType = source.equals("mining") ? "internal" : "external";
        double piRate = getPiRate(rateType);
        double amountInUSD = amountInPi * piRate;

        if (!isValidTransaction(merchantId, amountInUSD)) {
            throw new IllegalArgumentException("Jumlah transaksi tidak valid");
        }

        TransactionDTO transaction = new TransactionDTO();
        transaction.setId(generateTransactionId());
        transaction.setUserId(userId);
        transaction.setMerchantId(merchantId);
        transaction.setAmount(amountInPi);
        transaction.setUsdAmount(amountInUSD);
        transaction.setRateType(rateType);
        transaction.setSource(source);
        transaction.setStatus("pending");
        mongoTemplate.save(transaction, "transactions");
        return transaction;
    }

    public TransactionDTO executeTransaction(String transactionId) {
        TransactionDTO transaction = mongoTemplate.findById(transactionId, TransactionDTO.class, "transactions");
        if (transaction != null) {
            transaction.setStatus("completed");
            mongoTemplate.save(transaction, "transactions");
        }
        return transaction;
    }

    public List<TransactionDTO> getMerchantTransactions(String merchantId) {
        return mongoTemplate.find(Query.query(Criteria.where("merchantId").is(merchantId)), TransactionDTO.class, "transactions");
    }

    private double getPiRate(String type) {
        String url = "http://rate-service/api/rate/pi?type=" + type;
        RateDTO rate = restTemplate.getForObject(url, RateDTO.class);
        return rate != null ? rate.getRate() : (type.equals("internal") ? 314159.0 : 0.8111);
    }

    private boolean isValidTransaction(String merchantId, double amountInUSD) {
        return amountInUSD >= 0.01;
    }

    private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis();
    }
        }
