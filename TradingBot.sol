// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./DecentralizedExchange.sol";

contract TradingBot {
    DecentralizedExchange public dex;
    address public owner;

    struct Strategy {
        uint256 orderId;
        uint256 targetPrice;
        bool isBuyStrategy;
        bool isActive;
    }

    mapping(uint256 => Strategy) public strategies;
    uint256 public strategyCount;

    event StrategyCreated(uint256 indexed strategyId, uint256 targetPrice, bool isBuyStrategy);
    event StrategyExecuted(uint256 indexed strategyId, uint256 orderId);
    event StrategyCancelled(uint256 indexed strategyId);

    constructor(address _dex) {
        dex = DecentralizedExchange(_dex);
        owner = msg.sender;
    }

    modifier onlyOwner() {
        require(msg.sender == owner, "Not the contract owner");
        _;
    }

    function createStrategy(uint256 targetPrice, bool isBuyStrategy) public onlyOwner {
        strategyCount++;
        strategies[strategyCount] = Strategy(0, targetPrice, isBuyStrategy, true);
        emit StrategyCreated(strategyCount, targetPrice, isBuyStrategy);
    }

    function executeStrategy(uint256 strategyId) public {
        Strategy storage strategy = strategies[strategyId];
        require(strategy.isActive, "Strategy is not active");

        // Logic to check current price and execute order
        uint256 currentPrice = dex.getCurrentPrice(); // Assume this function exists in DEX
        if ((strategy.isBuyStrategy && currentPrice <= strategy.targetPrice) ||
            (!strategy.isBuyStrategy && currentPrice >= strategy.targetPrice)) {
            // Place order logic
            uint256 orderId = dex.placeOrder(1, currentPrice, strategy.isBuyStrategy); // Example order
            strategy.orderId = orderId;
            emit StrategyExecuted(strategyId, orderId);
        }
    }

    function cancelStrategy(uint256 strategyId) public onlyOwner {
        strategies[strategyId].isActive = false;
        emit StrategyCancelled(strategyId);
    }
}
