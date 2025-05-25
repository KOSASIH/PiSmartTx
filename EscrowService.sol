pragma solidity ^0.8.0;

contract EscrowService {
    address public buyer;
    address public seller;
    uint256 public amount;
    bool public buyerApproved;
    bool public sellerApproved;

    event EscrowCreated(address indexed buyer, address indexed seller, uint256 amount);
    event EscrowCompleted(address indexed buyer, address indexed seller);

    constructor(address _seller) {
        seller = _seller;
    }

    function createEscrow() public payable {
        require(msg.value > 0, "Amount must be greater than zero");
        buyer = msg.sender;
        amount = msg.value;
        emit EscrowCreated(buyer, seller, amount);
    }

    function approve() public {
        require(msg.sender == buyer || msg.sender == seller, "Not a party to the escrow");
        if (msg.sender == buyer) {
            buyerApproved = true;
        } else {
            sellerApproved = true;
        }
        if (buyerApproved && sellerApproved) {
            completeEscrow();
        }
    }

    function completeEscrow() private {
        payable(seller).transfer(amount);
        emit EscrowCompleted(buyer, seller);
    }
}
