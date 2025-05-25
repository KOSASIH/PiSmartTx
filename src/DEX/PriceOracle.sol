// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/access/Ownable.sol";

contract PriceOracle is Ownable {
    struct PriceInfo {
        uint256 price; // Price in USD cents (e.g., $314,159.00 = 314159)
        uint256 timestamp; // Timestamp of the last price update
    }

    mapping(address => PriceInfo) public prices; // Mapping of token addresses to their prices
    address public piCoinAddress; // Address of the Pi Coin token
    bool public priceUpdatesPaused; // Flag to pause price updates

    event PriceUpdated(address indexed token, uint256 price, uint256 timestamp);
    event PriceUpdatePaused();
    event PriceUpdateResumed();

    // Fixed price for Pi Coin
    uint256 public constant PI_PRICE = 314159; // Price of 1 Pi in USD cents (three hundred fourteen thousand one hundred fifty-nine dollars)

    constructor(address _piCoinAddress) {
        piCoinAddress = _piCoinAddress;
        prices[piCoinAddress] = PriceInfo(PI_PRICE, block.timestamp);
        emit PriceUpdated(piCoinAddress, PI_PRICE, block.timestamp);
    }

    // Function to update the price of a token
    function updatePrice(address token, uint256 price) public onlyOwner {
        require(!priceUpdatesPaused, "Price updates are paused");
        prices[token] = PriceInfo(price, block.timestamp);
        emit PriceUpdated(token, price, block.timestamp);
    }

    // Function to get the price of a token
    function getPrice(address token) public view returns (uint256 price, uint256 timestamp) {
        PriceInfo storage priceInfo = prices[token];
        return (priceInfo.price, priceInfo.timestamp);
    }

    // Function to get the price of Pi Coin
    function getPiCoinPrice() public view returns (uint256 price, uint256 timestamp) {
        return getPrice(piCoinAddress);
    }

    // Function to add a new token and set its initial price
    function addToken(address token, uint256 price) public onlyOwner {
        require(prices[token].price == 0, "Token already exists");
        prices[token] = PriceInfo(price, block.timestamp);
        emit PriceUpdated(token, price, block.timestamp);
    }

    // Function to pause price updates
    function pausePriceUpdates() public onlyOwner {
        priceUpdatesPaused = true;
        emit PriceUpdatePaused();
    }

    // Function to resume price updates
    function resumePriceUpdates() public onlyOwner {
        priceUpdatesPaused = false;
        emit PriceUpdateResumed();
    }
}
