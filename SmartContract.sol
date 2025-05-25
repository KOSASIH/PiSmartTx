pragma solidity ^0.8.0;

contract PiTransaction {
    mapping(address => uint256) public balances;

    event TransactionExecuted(address indexed from, address indexed to, uint256 amount);

    function transfer(address to, uint256 amount) public {
        require(balances[msg.sender] >= amount, "Insufficient balance");
        balances[msg.sender] -= amount;
        balances[to] += amount;
        emit TransactionExecuted(msg.sender, to, amount);
    }

    function deposit() public payable {
        balances[msg.sender] += msg.value;
    }
}
