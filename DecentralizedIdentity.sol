pragma solidity ^0.8.0;

contract DecentralizedIdentity {
    struct User {
        string name;
        string email;
        bool isVerified;
    }

    mapping(address => User) public users;

    event UserRegistered(address indexed userAddress, string name, string email);
    event UserVerified(address indexed userAddress);

    function registerUser (string memory name, string memory email) public {
        users[msg.sender] = User(name, email, false);
        emit UserRegistered(msg.sender, name, email);
    }

    function verifyUser (address userAddress) public {
        users[userAddress].isVerified = true;
        emit UserVerified(userAddress);
    }
}
