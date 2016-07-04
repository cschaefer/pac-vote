package com.prodyna.pac.vote.service.decorator;

import com.prodyna.pac.vote.service.api.BallotService;
import com.prodyna.pac.vote.service.api.model.Ballot;
import com.prodyna.pac.vote.service.exceptions.NoBallotOwnerException;
import com.prodyna.pac.vote.service.exceptions.UnauthorizedUserException;
import com.prodyna.pac.vote.service.exceptions.UnknownBallotException;
import com.prodyna.pac.vote.service.exceptions.UnknownUserException;

import java.util.List;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.slf4j.Logger;

/**
 * Decorate {@link BallotService} with logging.
 * 
 * @author cschaefer
 *
 */
@Decorator
public abstract class BallotServiceDecorator implements BallotService {

    @Inject
    private Logger logger;
    
    @Inject
    @Delegate
    private BallotService ballotService;
    
    @Override
    public List<Ballot> findAll() {
        logger.info("findAll start ...");
        try {
            return ballotService.findAll();            
        } finally {
            logger.info("findAll done.");
        }
    }

    @Override
    public Ballot create(Ballot ballot) throws NoBallotOwnerException, UnknownUserException {
        logger.info("create start ...");
        try {
            logger.info("create [{}]", ballot);
            Ballot createdBallot = ballotService.create(ballot);
            logger.info("created [{}]", ballot);
            return createdBallot;
        } catch (NoBallotOwnerException e) {
            logger.error("No ballot owner for [{}]", ballot);
            throw e;
        } catch (UnknownUserException e) {
            logger.error("Owner is not known [{}]", ballot);
            throw e;
        } finally {
            logger.info("create done.");
        }
    }

    @Override
    public Ballot findById(Integer ballotId) {
        logger.info("findById start ...");
        try {
            logger.info("finding with id=[{}]", ballotId);
            Ballot foundBallot = ballotService.findById(ballotId);
            logger.info("found [{}]", foundBallot);
            return foundBallot;
        } finally {
            logger.info("findById done.");
        }
    }

    @Override
    public Ballot update(Integer ballotId, Ballot ballot) throws UnauthorizedUserException, UnknownBallotException {
        logger.info("update start ...");
        try {
            logger.info("updating ballotId=[{}], ballot=[{}]", ballotId, ballot);
            Ballot updatedBallot = ballotService.update(ballotId, ballot);
            logger.info("updated ballot=[{}]", updatedBallot);
            return updatedBallot;
        } catch (UnauthorizedUserException e) {
            logger.error("User is not admin or ballot owner for [{}]", ballot);
            throw e;
        } catch (UnknownBallotException e) {
            logger.error("Ballot is not known [{}]", ballot);
            throw e;
        } finally {
            logger.info("update done.");
        }
    }

    @Override
    public void delete(Integer ballotId) throws UnauthorizedUserException, UnknownBallotException {
        logger.info("delete start ...");
        try {
            logger.info("deleting ballotId=[{}]", ballotId);
            ballotService.delete(ballotId);
            logger.info("deleted ballotId=[{}]", ballotId);
        } catch (UnauthorizedUserException e) {
            logger.error("User is not admin or ballot owner for [{}]", ballotId);
            throw e;
        } catch (UnknownBallotException e) {
            logger.error("Ballot is not known [{}]", ballotId);
            throw e;
        } finally {
            logger.info("delete done.");
        }
        
    }

    
    
}
