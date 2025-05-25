pragma solidity ^0.8.0;

contract FeedbackSystem {
    struct Feedback {
        address user;
        uint8 rating; // 1 to 5
        string comment;
    }

    Feedback[] public feedbacks;

    event FeedbackSubmitted(address indexed user, uint8 rating, string comment);

    function submitFeedback(uint8 rating, string memory comment) public {
        require(rating >= 1 && rating <= 5, "Rating must be between 1 and 5");
        feedbacks.push(Feedback(msg.sender, rating, comment));
        emit FeedbackSubmitted(msg.sender, rating, comment);
    }
}
