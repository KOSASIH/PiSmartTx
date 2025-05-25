pragma solidity ^0.8.0;

contract LendingPlatform {
    struct Loan {
        address borrower;
        uint256 amount;
        uint256 interestRate;
        uint256 dueDate;
        bool repaid;
    }

    mapping(uint256 => Loan) public loans;
    uint256 public loanCount;

    event LoanCreated(uint256 indexed loanId, address indexed borrower, uint256 amount, uint256 interestRate, uint256 dueDate);
    event LoanRepaid(uint256 indexed loanId);

    function createLoan(uint256 amount, uint256 interestRate, uint256 duration) public {
        loanCount++;
        loans[loanCount] = Loan(msg.sender, amount, interestRate, block.timestamp + duration, false);
        emit LoanCreated(loanCount, msg.sender, amount, interestRate, block.timestamp + duration);
    }

    function repayLoan(uint256 loanId) public payable {
        Loan storage loan = loans[loanId];
        require(msg.sender == loan.borrower, "Only borrower can repay the loan");
        require(!loan.repaid, "Loan already repaid");
        require(msg.value >= loan.amount + (loan.amount * loan.interestRate / 100), "Insufficient repayment amount");

        loan.repaid = true;
        emit LoanRepaid(loanId);
        // Logic to transfer funds to the lender
    }
}
