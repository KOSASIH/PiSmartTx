pragma solidity ^0.8.0;

contract VotingSystem {
    struct Candidate {
        string name;
        uint256 voteCount;
    }

    mapping(uint256 => Candidate) public candidates;
    mapping(address => bool) public hasVoted;
    uint256 public candidateCount;

    event CandidateAdded(uint256 indexed candidateId, string name);
    event Voted(uint256 indexed candidateId);

    function addCandidate(string memory name) public {
        candidateCount++;
        candidates[candidateCount] = Candidate(name, 0);
        emit CandidateAdded(candidateCount, name);
    }

    function vote(uint256 candidateId) public {
        require(!hasVoted[msg.sender], "You have already voted");
        require(candidateId > 0 && candidateId <= candidateCount, "Invalid candidate ID");

        candidates[candidateId].voteCount++;
        hasVoted[msg.sender] = true;
        emit Voted(candidateId);
    }

    function getResults() public view returns (string memory winnerName, uint256 winnerVoteCount) {
        uint256 winningVoteCount = 0;
        for (uint256 i = 1; i <= candidateCount; i++) {
            if (candidates[i].voteCount > winningVoteCount) {
                winningVoteCount = candidates[i].voteCount;
                winnerName = candidates[i].name;
            }
        }
        winnerVoteCount = winningVoteCount;
    }
}
