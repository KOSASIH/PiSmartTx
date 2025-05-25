pragma solidity ^0.8.0;

contract InsuranceProtocol {
    struct Policy {
        address policyholder;
        uint256 premium;
        uint256 coverageAmount;
        bool isActive;
    }

    mapping(uint256 => Policy) public policies;
    uint256 public policyCount;

    event PolicyPurchased(uint256 indexed policyId, address indexed policyholder, uint256 premium, uint256 coverageAmount);
    event ClaimFiled(uint256 indexed policyId, uint256 claimAmount);

    function purchasePolicy(uint256 premium, uint256 coverageAmount) public {
        policyCount++;
        policies[policyCount] = Policy(msg.sender, premium, coverageAmount, true);
        emit PolicyPurchased(policyCount, msg.sender, premium, coverageAmount);
    }

    function fileClaim(uint256 policyId, uint256 claimAmount) public {
        Policy storage policy = policies[policyId];
        require(msg.sender == policy.policyholder, "Only policyholder can file a claim");
        require(policy.isActive, "Policy is not active");
        require(claimAmount <= policy.coverageAmount, "Claim exceeds coverage amount");

        policy.isActive = false; // Mark policy as inactive after claim
        emit ClaimFiled(policyId, claimAmount);
        // Logic to transfer funds to the policyholder
    }
}
