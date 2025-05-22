// backend/smartcontract-service/src/main/java/com/pistore/smartcontract/SmartContractController.java
package com.pistore.smartcontract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/smartcontract")
public class SmartContractController {
    @Autowired
    private SmartContractService smartContractService;

    @PostMapping("/create-transaction")
    public TransactionDTO createTransaction(@RequestBody TransactionRequest request) {
        return smartContractService.createTransaction(
            request.getUserId(), 
            request.getMerchantId(), 
            request.getAmount()
        );
    }

    @PostMapping("/execute-transaction")
    public TransactionDTO executeTransaction(@RequestBody String transactionId) {
        return smartContractService.executeTransaction(transactionId);
    }
}
