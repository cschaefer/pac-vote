package com.prodyna.pac.vote.service.repo.api;

import com.prodyna.pac.vote.service.api.model.ChoiceCount;
import com.prodyna.pac.vote.service.api.model.Vote;

import java.util.List;

public interface VoteRepository {

    List<Vote> findAll();

    Vote create(Vote vote);

    Vote findById(Integer voteId);

    void delete(Integer voteId);

    List<ChoiceCount> findBallotResult(Integer ballotId);

    List<Vote> findMyBallotVotes(Integer userId);

    List<Vote> findBallotVotes(Integer ballotId);

    Boolean hasUserVoted(Integer userId, Integer ballotId);
    
}
