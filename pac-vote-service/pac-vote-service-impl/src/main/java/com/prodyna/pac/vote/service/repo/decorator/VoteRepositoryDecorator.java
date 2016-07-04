package com.prodyna.pac.vote.service.repo.decorator;

import com.prodyna.pac.vote.service.api.model.ChoiceCount;
import com.prodyna.pac.vote.service.api.model.Vote;
import com.prodyna.pac.vote.service.repo.api.VoteRepository;

import java.util.List;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.slf4j.Logger;

@Decorator
public class VoteRepositoryDecorator implements VoteRepository {

    @Inject
    private Logger logger;

    @Inject
    @Delegate
    private VoteRepository voteRepo;

    @Override
    public List<Vote> findAll() {
        return this.voteRepo.findAll();
    }

    @Override
    public Vote create(Vote vote) {
        this.logger.info("creating vote=[{}]", vote);
        final Vote createdVote = this.voteRepo.create(vote);
        this.logger.info("created createdVote=[{}]", createdVote);
        return createdVote;
    }

    @Override
    public Vote findById(Integer voteId) {
        return this.voteRepo.findById(voteId);
    }

    @Override
    public void delete(Integer voteId) {
        this.voteRepo.delete(voteId);
    }

    @Override
    public List<ChoiceCount> findBallotResult(Integer ballotId) {
        return this.voteRepo.findBallotResult(ballotId);
    }

    @Override
    public List<Vote> findMyBallotVotes(Integer userId) {
        this.logger.info("findMyBallotVote userId=[{}]", userId);
        return this.voteRepo.findMyBallotVotes(userId);
    }

    @Override
    public List<Vote> findBallotVotes(Integer ballotId) {
        return this.voteRepo.findBallotVotes(ballotId);
    }

    @Override
    public Boolean hasUserVoted(Integer userId, Integer ballotId) {
        return this.voteRepo.hasUserVoted(userId, ballotId);
    }

}
