package com.pistore.rate;

import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import java.math.BigInteger;

@Component
public class ChainlinkOracleClient {
    private final Web3j web3j = Web3j.build(new HttpService("https://eth-mainnet.g.alchemy.com/v2/your_alchemy_key"));
    private static final String CHAINLINK_PI_USDT_ORACLE = "0xYourChainlinkOracleAddress";

    public double getPiPriceFromOracle() {
        try {
            // Contoh: Panggil smart contract Chainlink untuk harga PI/USDT
            // Ganti dengan ABI dan logika kontrak sebenarnya
            BigInteger price = callOracleContract();
            return price.doubleValue() / 1e8; // Asumsi 8 desimal
        } catch (Exception e) {
            System.err.println("Gagal mengambil harga dari oracle: " + e.getMessage());
            return 0.8111; // Fallback
        }
    }

    private BigInteger callOracleContract() {
        // Implementasi panggilan kontrak Chainlink
        // Placeholder: Ganti dengan logika Web3j
        return BigInteger.valueOf(81110000); // Contoh: $0.8111 * 1e8
    }
}
