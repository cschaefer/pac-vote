package com.prodyna.pac.vote.service.impl;

import com.prodyna.pac.vote.annotations.AuthenticatedUser;
import com.prodyna.pac.vote.annotations.Monitored;
import com.prodyna.pac.vote.service.api.BallotService;
import com.prodyna.pac.vote.service.api.model.Ballot;
import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.api.model.Vote;
import com.prodyna.pac.vote.service.exceptions.NoBallotOwnerException;
import com.prodyna.pac.vote.service.exceptions.UnauthorizedUserException;
import com.prodyna.pac.vote.service.exceptions.UnknownBallotException;
import com.prodyna.pac.vote.service.exceptions.UnknownUserException;
import com.prodyna.pac.vote.service.repo.api.BallotRepository;
import com.prodyna.pac.vote.service.repo.api.UserRepository;
import com.prodyna.pac.vote.service.repo.api.VoteRepository;

import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

/**
 * {@link BallotService} implementation.
 *
 * @author cschaefer
 *
 */
@Monitored
@Provider
@Stateless
public class BallotServiceImpl implements BallotService {

    @Inject
    private Logger log;

    @Inject
    @AuthenticatedUser
    private User authenticatedUser;

    @Inject
    private BallotRepository repo;

    @Inject
    private UserRepository userRepo;

    @Inject
    private VoteRepository voteRepo;

    @Override
    public Ballot create(Ballot ballot) throws NoBallotOwnerException, UnknownUserException {

        if (Objects.isNull(ballot.getOwner())) {
            throw new NoBallotOwnerException("Owner of ballot cannot be null");
        }

        final User user = this.userRepo.findByUid(ballot.getOwner().getUid());
        if (Objects.isNull(user)) {
            throw new UnknownUserException("Owner of ballot cannot be found");
        }

        if (user.getUserId() != this.authenticatedUser.getUserId()) {
            throw new UnknownUserException("Authenticated user is not ballot owner");
        }

        ballot.setOwner(user);

        return this.repo.create(ballot);
    }

    @Override
    public Ballot findById(Integer ballotId) {
        final Ballot ballot = this.repo.findById(ballotId);

        this.upgradeBallot(ballot);

        return ballot;
    }

    @Override
    public List<Ballot> findAll() {
        final List<Ballot> allBallots = this.repo.findAll();

        for (final Ballot ballot : allBallots) {
            this.upgradeBallot(ballot);
        }

        return allBallots;

    }


    @Override
    public Ballot update(Integer ballotId, Ballot ballot) throws UnauthorizedUserException, UnknownBallotException {

        this.validateBallotAction(ballotId);
        this.validateUserAction(ballotId);

        return this.repo.update(ballotId, ballot);
    }

    @Override
    public void delete(Integer ballotId) throws UnauthorizedUserException, UnknownBallotException {

        this.validateBallotAction(ballotId);
        this.validateUserAction(ballotId);

        final List<Vote> votes = this.voteRepo.findBallotVotes(ballotId);
        if (Objects.nonNull(votes)) {
            for (final Vote vote : votes) {
                this.voteRepo.delete(vote.getVoteId());
            }
        }

        this.repo.delete(ballotId);

    }

    protected void upgradeBallot(Ballot ballot) {

        if (Objects.nonNull(this.authenticatedUser) && this.authenticatedUser.getUserId() != -1) {

            if (this.voteRepo.hasUserVoted(this.authenticatedUser.getUserId(), ballot.getBallotId())) {
                ballot.setVoted(true);
            }

            if ((ballot.getOwner().getUserId() == this.authenticatedUser.getUserId())
                    || this.authenticatedUser.isAdministrator()) {
                ballot.setEdit(true);
            }

        }

    }

    protected void validateBallotAction(final Integer ballotId) throws UnknownBallotException {

        final Ballot currentBallot = this.repo.findById(ballotId);
        if (Objects.isNull(currentBallot)) {
            throw new UnknownBallotException("Ballot with id "+ ballotId + " does not exist");
        }

    }

    protected void validateUserAction(final Integer ballotId) throws UnauthorizedUserException {
        // pull the current ballot out to double check the user
        final Ballot currentBallot = this.repo.findById(ballotId);

        if (Objects.isNull(currentBallot)) {
            throw new IllegalStateException("Cannot validate user action without a ballot");
        }

        if ((currentBallot.getOwner().getUserId() != this.authenticatedUser.getUserId())
                && !this.authenticatedUser.isAdministrator()) {
            this.log.error("User [{}] not authorized to perform action on ballot [{}]", this.authenticatedUser, currentBallot);
            throw new UnauthorizedUserException("User not authorized to perform action");
        }

    }

}
