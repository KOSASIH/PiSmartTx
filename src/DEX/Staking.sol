// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract Staking is Ownable {
    IERC20 public stakingToken;
    uint256 public rewardRate; // Reward rate per block
    mapping(address => uint256) public stakes; // User stakes
    mapping(address => uint256) public rewards; // User rewards

    event Staked(address indexed user, uint256 amount);
    event Unstaked(address indexed user, uint256 amount);
    event RewardPaid(address indexed user, uint256 reward);

    constructor(address _stakingToken, uint256 _rewardRate) {
        stakingToken = IERC20(_stakingToken);
        rewardRate = _rewardRate;
    }

    function stake(uint256 amount) public {
        require(amount > 0, "Amount must be greater than zero");
        stakes[msg.sender] += amount;
        require(stakingToken.transferFrom(msg.sender, address(this), amount), "Transfer failed");
        emit Staked(msg.sender, amount);
    }

    function unstake(uint256 amount) public {
        require(stakes[msg.sender] >= amount, "Insufficient stake");
        stakes[msg.sender] -= amount;
        require(stakingToken.transfer(msg.sender, amount), "Transfer failed");
        emit Unstaked(msg.sender, amount);
    }

    function claimReward() public {
        uint256 reward = stakes[msg.sender] * rewardRate;
        rewards[msg.sender] += reward;
        emit RewardPaid(msg.sender, reward);
    }
}
