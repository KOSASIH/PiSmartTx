// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/math/SafeMath.sol";

contract DecentralizedExchange is Ownable {
    using SafeMath for uint256;

    struct Order {
        address trader;
        uint256 amount;
        uint256 price; // Price in terms of the token being bought
        bool isBuyOrder; // True if buy order, false if sell order
        uint256 filled; // Amount filled
    }

    struct LiquidityPool {
        uint256 reserveA; // Reserve of token A
        uint256 reserveB; // Reserve of token B
        uint256 totalSupply; // Total supply of LP tokens
        uint256 fee; // Trading fee (in basis points)
    }

    mapping(address => LiquidityPool) public liquidityPools; // Mapping of token pairs to liquidity pools
    mapping(uint256 => Order) public orders; // Mapping of order ID to orders
    uint256 public orderCount; // Total number of orders

    IERC20 public tokenA; // Token A being traded
    IERC20 public tokenB; // Token B being used for payment

    event OrderPlaced(uint256 indexed orderId, address indexed trader, uint256 amount, uint256 price, bool isBuyOrder);
    event OrderExecuted(uint256 indexed orderId, address indexed trader, uint256 amount, uint256 price);
    event OrderCancelled(uint256 indexed orderId, address indexed trader);
    event LiquidityAdded(address indexed provider, uint256 amountA, uint256 amountB);
    event LiquidityRemoved(address indexed provider, uint256 amountA, uint256 amountB);
    event FeeUpdated(uint256 newFee);

    constructor(address _tokenA, address _tokenB) {
        tokenA = IERC20(_tokenA);
        tokenB = IERC20(_tokenB);
    }

    // Function to add liquidity to the pool
    function addLiquidity(uint256 amountA, uint256 amountB) public {
        require(amountA > 0 && amountB > 0, "Amounts must be greater than zero");

        // Transfer tokens from the provider to the contract
        require(tokenA.transferFrom(msg.sender, address(this), amountA), "Transfer failed");
        require(tokenB.transferFrom(msg.sender, address(this), amountB), "Transfer failed");

        // Update the liquidity pool
        LiquidityPool storage pool = liquidityPools[address(tokenA)];
        pool.reserveA += amountA;
        pool.reserveB += amountB;
        pool.totalSupply += amountA + amountB; // Simplified LP token accounting

        emit LiquidityAdded(msg.sender, amountA, amountB);
    }

    // Function to remove liquidity from the pool
    function removeLiquidity(uint256 amountA, uint256 amountB) public {
        LiquidityPool storage pool = liquidityPools[address(tokenA)];
        require(amountA > 0 && amountB > 0, "Amounts must be greater than zero");
        require(pool.reserveA >= amountA && pool.reserveB >= amountB, "Insufficient liquidity");

        // Transfer tokens back to the provider
        require(tokenA.transfer(msg.sender, amountA), "Transfer failed");
        require(tokenB.transfer(msg.sender, amountB), "Transfer failed");

        // Update the liquidity pool
        pool.reserveA -= amountA;
        pool.reserveB -= amountB;
        pool.totalSupply -= amountA + amountB; // Simplified LP token accounting

        emit LiquidityRemoved(msg.sender, amountA, amountB);
    }

    // Function to place an order
    function placeOrder(uint256 amount, uint256 price, bool isBuyOrder) public {
        require(amount > 0, "Amount must be greater than zero");
        require(price > 0, "Price must be greater than zero");

        if (isBuyOrder) {
            require(tokenB.transferFrom(msg.sender, address(this), amount.mul(price)), "Transfer failed");
        } else {
            require(tokenA.transferFrom(msg.sender, address(this), amount), "Transfer failed");
        }

        orders[orderCount] = Order(msg.sender, amount, price, isBuyOrder, 0);
        emit OrderPlaced(orderCount, msg.sender, amount, price, isBuyOrder);
        orderCount++;
    }

    // Function to execute an order
    function executeOrder(uint256 orderId) public {
        Order storage order = orders[orderId];
        require(order.trader != address(0), "Order does not exist");
        require(order.filled < order.amount, "Order already fully filled");

        uint256 amountToFill = order.amount.sub(order.filled);
        uint256 totalCost = amountToFill.mul(order.price).mul(10000 - getFee(order.isBuyOrder)).div(10000);

        if (order.isBuyOrder) {
            require(tokenA.balanceOf(msg.sender) >= amountToFill, "Insufficient token balance");
            require(tokenB.transfer(msg.sender, totalCost), "Transfer failed");
            require(tokenA.transferFrom(msg.sender, order.trader, amountToFill), "Transfer failed");
        } else {
            require(tokenB.balanceOf(msg.sender) >= totalCost, "Insufficient token balance");
            require(tokenA.transfer(msg.sender, amountToFill), "Transfer failed");
            require(tokenB.transferFrom(msg.sender, order.trader, totalCost), "Transfer failed");
        }

        order.filled += amountToFill;
        emit OrderExecuted(orderId, order.trader, amountToFill, order.price);
    }

    // Function to cancel an order
    function cancelOrder(uint256 orderId) public {
        Order storage order = orders[orderId];
        require(order.trader == msg.sender, "Only the order creator can cancel the order");
        require(order.filled == 0, "Cannot cancel a filled order");

        if (order.isBuyOrder) {
            require(tokenB.transfer(msg.sender, order.amount.mul(order.price)), "Transfer failed");
        } else {
            require(tokenA.transfer(msg.sender, order.amount), "Transfer failed");
        }

        emit OrderCancelled(orderId, msg.sender);
        delete orders[orderId]; // Remove the order
    }

    // Function to get order details
    function getOrder(uint256 orderId) public view returns (Order memory) {
        return orders[orderId];
    }

    // Function to get the total number of orders
    function getOrdersCount() public view returns (uint256) {
        return orderCount;
    }

    // Function to get the current reserves of the liquidity pool
    function getReserves() public view returns (uint256 reserveA, uint256 reserveB) {
        LiquidityPool storage pool = liquidityPools[address(tokenA)];
        return (pool.reserveA, pool.reserveB);
    }

    // Function to set the trading fee
    function setFee(uint256 newFee) public onlyOwner {
        require(newFee <= 1000, "Fee must be less than or equal to 10%");
        LiquidityPool storage pool = liquidityPools[address(tokenA)];
        pool.fee = newFee;
        emit FeeUpdated(newFee);
    }

    // Function to get the trading fee
    function getFee(bool isBuyOrder) public view returns (uint256) {
        LiquidityPool storage pool = liquidityPools[address(tokenA)];
        return pool.fee;
    }

    // Function to execute flash loans
    function flashLoan(address token, uint256 amount) external {
        require(amount > 0, "Amount must be greater than zero");
        require(token == address(tokenA) || token == address(tokenB), "Invalid token");

        // Transfer the tokens to the borrower
        require(IERC20(token).transfer(msg.sender, amount), "Transfer failed");

        // Execute the borrower's logic (should be implemented in the borrower's contract)
        // The borrower must pay back the loan plus fee
        uint256 fee = amount.mul(3).div(1000); // 0.3% fee
        require(IERC20(token).transferFrom(msg.sender, address(this), amount.add(fee)), "Loan not paid back");
    }
}
