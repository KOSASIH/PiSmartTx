// frontend/src/contracts/PiStore.sol
pragma solidity ^0.8.0;

contract PiStore {
    struct Transaction {
        address user;
        address merchant;
        uint256 amount;
        string paymentType;
        bool autoConvert;
        uint256 timestamp;
    }

    Transaction[] public transactions;

    event TransactionRecorded(
        address indexed user,
        address indexed merchant,
        uint256 amount,
        string paymentType,
        bool autoConvert,
        uint256 timestamp
    );

    function submitTransaction(
        address user,
        address merchant,
        uint256 amount,
        string memory paymentType,
        bool autoConvert
    ) external {
        transactions.push(Transaction(user, merchant, amount, paymentType, autoConvert, block.timestamp));
        emit TransactionRecorded(user, merchant, amount, paymentType, autoConvert, block.timestamp);
    }

    function getUserTransactions(address user) external view returns (Transaction[] memory) {
        uint256 count = 0;
        for (uint256 i = 0; i < transactions.length; i++) {
            if (transactions[i].user == user || transactions[i].merchant == user) {
                count++;
            }
        }

        Transaction[] memory result = new Transaction[](count);
        uint256 index = 0;
        for (uint256 i = 0; i < transactions.length; i++) {
            if (transactions[i].user == user || transactions[i].merchant == user) {
                result[index] = transactions[i];
                index++;
            }
        }
        return result;
    }
}
