pragma solidity ^0.8.0;

contract ContentPublishing {
    struct Content {
        string title;
        string uri;
        address creator;
        uint256 price;
    }

    mapping(uint256 => Content) public contents;
    uint256 public contentCount;

    event ContentPublished(uint256 indexed contentId, string title, string uri, address indexed creator, uint256 price);
    event ContentPurchased(uint256 indexed contentId, address indexed buyer);

    function publishContent(string memory title, string memory uri, uint256 price) public {
        contentCount++;
        contents[contentCount] = Content(title, uri, msg.sender, price);
        emit ContentPublished(contentCount, title, uri, msg.sender, price);
    }

    function purchaseContent(uint256 contentId) public payable {
        Content storage content = contents[contentId];
        require(msg.value >= content.price, "Insufficient funds to purchase content");
        emit ContentPurchased(contentId, msg.sender);
        // Logic to transfer funds to the creator
    }
}
