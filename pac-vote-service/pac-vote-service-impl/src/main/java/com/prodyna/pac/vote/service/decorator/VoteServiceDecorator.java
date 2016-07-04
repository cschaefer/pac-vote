package com.prodyna.pac.vote.service.decorator;

import com.prodyna.pac.vote.service.api.VoteService;
import com.prodyna.pac.vote.service.api.dto.VoteDto;
import com.prodyna.pac.vote.service.api.model.ChoiceCount;
import com.prodyna.pac.vote.service.api.model.Vote;
import com.prodyna.pac.vote.service.exceptions.InvalidChoiceException;
import com.prodyna.pac.vote.service.exceptions.UnknownBallotException;
import com.prodyna.pac.vote.service.exceptions.UnknownChoiceException;
import com.prodyna.pac.vote.service.exceptions.UserAlreadyVotedException;
import com.prodyna.pac.vote.service.exceptions.UserNotVotedException;

import java.util.List;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.slf4j.Logger;

/**
 * Decorates {@link VoteService} with logging.
 *
 * @author cschaefer
 *
 */
@Decorator
public abstract class VoteServiceDecorator implements VoteService {

    @Inject
    private Logger logger;

    @Inject
    @Delegate
    private VoteService voteService;

    @Override
    public Vote vote(VoteDto voteDto) throws UnknownBallotException, UnknownChoiceException, InvalidChoiceException,
    UserAlreadyVotedException {
        this.logger.info("vote start ...");
        try {
            this.logger.info("voting with voteDto=[{}]", voteDto);
            return this.voteService.vote(voteDto);
        } catch (final UnknownBallotException e) {
            this.logger.error("Unknown ballot found in voteDto=[{}]", voteDto);
            throw e;
        } catch (final UnknownChoiceException e) {
            this.logger.error("Unknown choice found in voteDto=[{}]", voteDto);
            throw e;
        } catch (final InvalidChoiceException e) {
            this.logger.error("Invalid choice found in voteDto=[{}]", voteDto);
            throw e;
        } catch (final UserAlreadyVotedException e) {
            this.logger.error("Authorized user already voted for voteDto=[{}]", voteDto);
            throw e;
        } finally {
            this.logger.info("vote done.");
        }
    }

    @Override
    public List<VoteDto> findMyVotes() {
        this.logger.info("findMyVotes start ...");
        try {
            final List<VoteDto> findMyVotes = this.voteService.findMyVotes();
            this.logger.info("myVotes=[{}]", findMyVotes);
            return findMyVotes;
        } finally {
            this.logger.info("findMyVotes done.");
        }
    }

    @Override
    public List<ChoiceCount> findBallotVotes(Integer ballotId) throws UnknownBallotException, UserNotVotedException {
        this.logger.info("findBallotVotes start ...");
        try {
            this.logger.info("finding with ballotId=[{}]", ballotId);
            return this.voteService.findBallotVotes(ballotId);
        } catch (final UnknownBallotException e) {
            this.logger.error("Ballot does not exist. ballotId=[{}]", ballotId);
            throw e;
        } catch (final UserNotVotedException e) {
            this.logger.error("Authorized user cannot retrieve ballot when not voted. ballotId=[{}]", ballotId);
            throw e;
        } finally {
            this.logger.info("findBallotVotes done.");
        }
    }

}
