// backend/smartcontract-service/src/main/java/com/pistore/smartcontract/SmartContractController.java
package com.pistore.smartcontract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class SmartContractController {
    @Autowired
    private SmartContractService smartContractService;

    @PostMapping("/create-transaction")
    public TransactionDTO createTransaction(@RequestBody TransactionRequest request) {
        return smartContractService.createTransaction(
            request.getUserId(),
            request.getMerchantId(),
            request.getAmountInPi(),
            request.getSource()
        );
    }

    @PostMapping("/execute-transaction")
    public TransactionDTO executeTransaction(@RequestBody String transactionId) {
        return smartContractService.executeTransaction(transactionId);
    }

    @GetMapping("/merchant")
    public List<TransactionDTO> getMerchantTransactions(@RequestParam String merchantId) {
        return smartContractService.getMerchantTransactions(merchantId);
    }
}
