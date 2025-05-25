pragma solidity ^0.8.0;

interface IPriceFeed {
    function getLatestPrice() external view returns (uint256);
}

contract PriceFeedOracle {
    IPriceFeed public priceFeed;

    constructor(address _priceFeed) {
        priceFeed = IPriceFeed(_priceFeed);
    }

    function getCurrentPrice() public view returns (uint256) {
        return priceFeed.getLatestPrice();
    }
}
