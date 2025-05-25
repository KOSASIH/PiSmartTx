// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

interface IPriceFeed {
    function getLatestPrice() external view returns (uint256);
}

contract PriceFeedOracle {
    struct PriceFeedInfo {
        IPriceFeed priceFeed; // Price feed contract
        bool isActive; // Status of the price feed
    }

    mapping(address => PriceFeedInfo) public priceFeeds; // Mapping of price feed addresses to their info
    address[] public activePriceFeeds; // List of active price feeds
    bool public priceUpdatesPaused; // Flag to pause price updates

    // Fixed price for Pi Coin
    uint256 public constant PI_PRICE = 314159; // Price of 1 Pi in USD cents (three hundred fourteen thousand one hundred fifty-nine dollars)
    address public piCoinAddress; // Address of the Pi Coin token

    event PriceFeedAdded(address indexed priceFeed);
    event PriceFeedRemoved(address indexed priceFeed);
    event PriceUpdated(uint256 price, uint256 timestamp);
    event PriceUpdatePaused();
    event PriceUpdateResumed();

    constructor(address _piCoinAddress) {
        require(_piCoinAddress != address(0), "Invalid Pi Coin address");
        piCoinAddress = _piCoinAddress;
    }

    // Function to add a new price feed
    function addPriceFeed(address _priceFeed) public {
        require(_priceFeed != address(0), "Invalid address");
        require(!priceFeeds[_priceFeed].isActive, "Price feed already exists");

        priceFeeds[_priceFeed] = PriceFeedInfo(IPriceFeed(_priceFeed), true);
        activePriceFeeds.push(_priceFeed);
        emit PriceFeedAdded(_priceFeed);
    }

    // Function to remove a price feed
    function removePriceFeed(address _priceFeed) public {
        require(priceFeeds[_priceFeed].isActive, "Price feed does not exist");

        priceFeeds[_priceFeed].isActive = false;
        emit PriceFeedRemoved(_priceFeed);
    }

    // Function to get the current price from active price feeds
    function getCurrentPrice() public view returns (uint256) {
        require(activePriceFeeds.length > 0, "No active price feeds");

        uint256 totalPrice;
        uint256 activeCount;

        for (uint256 i = 0; i < activePriceFeeds.length; i++) {
            if (priceFeeds[activePriceFeeds[i]].isActive) {
                totalPrice += priceFeeds[activePriceFeeds[i]].priceFeed.getLatestPrice();
                activeCount++;
            }
        }

        require(activeCount > 0, "No active price feeds available");
        return totalPrice / activeCount; // Return the average price
    }

    // Function to get the price of Pi Coin
    function getPiCoinPrice() public view returns (uint256) {
        return PI_PRICE; // Return the fixed price of Pi Coin
    }

    // Function to pause price updates
    function pausePriceUpdates() public {
        priceUpdatesPaused = true;
        emit PriceUpdatePaused();
    }

    // Function to resume price updates
    function resumePriceUpdates() public {
        priceUpdatesPaused = false;
        emit PriceUpdateResumed();
    }
}
