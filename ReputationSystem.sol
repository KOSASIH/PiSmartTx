pragma solidity ^0.8.0;

contract ReputationSystem {
    mapping(address => uint256) public reputationScores;

    event ReputationUpdated(address indexed user, uint256 newScore);

    function updateReputation(address user, uint256 scoreChange) public {
        reputationScores[user] += scoreChange;
        emit ReputationUpdated(user, reputationScores[user]);
    }

    function getReputation(address user) public view returns (uint256) {
        return reputationScores[user];
    }
}
