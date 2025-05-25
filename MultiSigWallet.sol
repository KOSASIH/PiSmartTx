pragma solidity ^0.8.0;

contract MultiSigWallet {
    address[] public owners;
    mapping(uint256 => mapping(address => bool)) public approvals;
    uint256 public requiredApprovals;

    event TransactionCreated(uint256 indexed transactionId, address indexed creator);
    event TransactionApproved(uint256 indexed transactionId, address indexed owner);

    constructor(address[] memory _owners, uint256 _requiredApprovals) {
        owners = _owners;
        requiredApprovals = _requiredApprovals;
    }

    function createTransaction(uint256 transactionId) public {
        require(isOwner(msg.sender), "Not an owner");
        emit TransactionCreated(transactionId, msg.sender);
    }

    function approveTransaction(uint256 transactionId) public {
        require(isOwner(msg.sender), "Not an owner");
        approvals[transactionId][msg.sender] = true;
        emit TransactionApproved(transactionId, msg.sender);
    }

    function isOwner(address addr) internal view returns (bool) {
        for (uint256 i = 0; i < owners.length; i++) {
            if (owners[i] == addr) {
                return true;
            }
        }
        return false;
    }
}
