// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract Governance is Ownable {
    IERC20 public governanceToken;

    struct Proposal {
        string description;
        uint256 voteCount;
        mapping(address => bool) voted;
        bool executed;
    }

    mapping(uint256 => Proposal) public proposals;
    uint256 public proposalCount;

    event ProposalCreated(uint256 indexed proposalId, string description);
    event Voted(uint256 indexed proposalId, address indexed voter);
    event ProposalExecuted(uint256 indexed proposalId);

    constructor(address _governanceToken) {
        governanceToken = IERC20(_governanceToken);
    }

    function createProposal(string memory description) public {
        proposalCount++;
        proposals[proposalCount] = Proposal(description, 0, false);
        emit ProposalCreated(proposalCount, description);
    }

    function vote(uint256 proposalId) public {
        Proposal storage proposal = proposals[proposalId];
        require(!proposal.voted[msg.sender], "Already voted");
        require(!proposal.executed, "Proposal already executed");

        uint256 voterBalance = governanceToken.balanceOf(msg.sender);
        require(voterBalance > 0, "No voting power");

        proposal.voteCount += voterBalance;
        proposal.voted[msg.sender] = true;
        emit Voted(proposalId, msg.sender);
    }

    function executeProposal(uint256 proposalId) public onlyOwner {
        Proposal storage proposal = proposals[proposalId];
        require(!proposal.executed, "Proposal already executed");
        require(proposal.voteCount > 0, "No votes");

        // Logic to execute the proposal (e.g., change DEX parameters)
        proposal.executed = true;
        emit ProposalExecuted(proposalId);
    }
}
