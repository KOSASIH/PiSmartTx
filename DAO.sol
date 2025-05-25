pragma solidity ^0.8.0;

contract DAO {
    struct Proposal {
        string description;
        uint256 voteCount;
        mapping(address => bool) voters;
        bool executed;
    }

    mapping(uint256 => Proposal) public proposals;
    uint256 public proposalCount;
    address public owner;

    event ProposalCreated(uint256 indexed proposalId, string description);
    event Voted(uint256 indexed proposalId, address indexed voter);

    constructor() {
        owner = msg.sender;
    }

    function createProposal(string memory description) public {
        require(msg.sender == owner, "Only owner can create proposals");
        proposalCount++;
        proposals[proposalCount] = Proposal(description, 0, false);
        emit ProposalCreated(proposalCount, description);
    }

    function vote(uint256 proposalId) public {
        Proposal storage proposal = proposals[proposalId];
        require(!proposal.voters[msg.sender], "You have already voted");
        proposal.voters[msg.sender] = true;
        proposal.voteCount++;
        emit Voted(proposalId, msg.sender);
    }

    function executeProposal(uint256 proposalId) public {
        Proposal storage proposal = proposals[proposalId];
        require(proposal.voteCount > 0, "No votes for this proposal");
        require(!proposal.executed, "Proposal already executed");
        proposal.executed = true;
        // Execute proposal logic here
    }
}
