pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";

contract NFTMarketplace is ERC721 {
    struct NFT {
        uint256 id;
        string uri;
        address owner;
        uint256 price;
    }

    mapping(uint256 => NFT) public nfts;
    uint256 public nftCount;

    event NFTCreated(uint256 indexed id, string uri, address indexed owner, uint256 price);
    event NFTSold(uint256 indexed id, address indexed buyer);

    constructor() ERC721("NFTMarketplace", "NFTM") {}

    function createNFT(string memory uri, uint256 price) public {
        nftCount++;
        nfts[nftCount] = NFT(nftCount, uri, msg.sender, price);
        _mint(msg.sender, nftCount);
        emit NFTCreated(nftCount, uri, msg.sender, price);
    }

    function buyNFT(uint256 id) public payable {
        NFT storage nft = nfts[id];
        require(msg.value >= nft.price, "Insufficient funds to buy NFT");
        require(nft.owner != msg.sender, "Cannot buy your own NFT");

        address previousOwner = nft.owner;
        nft.owner = msg.sender;
        _transfer(previousOwner, msg.sender, id);
        emit NFTSold(id, msg.sender);
        payable(previousOwner).transfer(msg.value);
    }
}
