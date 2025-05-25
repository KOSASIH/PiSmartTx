pragma solidity ^0.8.0;

contract DataStorage {
    struct Data {
        string key;
        string value;
        address owner;
    }

    mapping(uint256 => Data) public dataStore;
    uint256 public dataCount;

    event DataStored(uint256 indexed dataId, string key, string value, address indexed owner);

    function storeData(string memory key, string memory value) public {
        dataCount++;
        dataStore[dataCount] = Data(key, value, msg.sender);
        emit DataStored(dataCount, key, value, msg.sender);
    }

    function retrieveData(uint256 dataId) public view returns (string memory key, string memory value) {
        Data storage data = dataStore[dataId];
        return (data.key, data.value);
    }
}
