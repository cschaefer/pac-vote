package com.prodyna.pac.vote.service.api;

import com.prodyna.pac.vote.annotations.Secured;
import com.prodyna.pac.vote.service.api.dto.VoteDto;
import com.prodyna.pac.vote.service.api.model.ChoiceCount;
import com.prodyna.pac.vote.service.api.model.Vote;
import com.prodyna.pac.vote.service.exceptions.InvalidChoiceException;
import com.prodyna.pac.vote.service.exceptions.UnknownBallotException;
import com.prodyna.pac.vote.service.exceptions.UnknownChoiceException;
import com.prodyna.pac.vote.service.exceptions.UserAlreadyVotedException;
import com.prodyna.pac.vote.service.exceptions.UserNotVotedException;

import java.util.List;

import javax.ejb.Local;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * {@code VoteService} allows authorized {@code User}s to cast a {@code Vote} for a
 * given {@code Ballot}.
 *
 * @author cschaefer
 *
 */
@Local
@Path("/vote")
@Consumes("application/json")
@Produces("application/json")
public interface VoteService {

    /**
     * Secured operation to cast a {@link Vote}. The user casting the vote is identified by the
     * authentication filter.
     *
     * @param voteDto transfer object defining the ballot and choice combination.
     * @return the {@link Vote} cast
     * @throws UnknownBallotException the ballotId given is not known
     * @throws UnknownChoiceException the choiceId given is not known or ballot has no choices
     * @throws InvalidChoiceException the choiceId does not belong to the ballot
     * @throws UserAlreadyVotedException the user has already vote for this ballot
     */
    @POST
    @Secured
    Vote vote(VoteDto voteDto) throws UnknownBallotException, UnknownChoiceException, InvalidChoiceException, UserAlreadyVotedException;

    /**
     * Secured operation to find a {@code User}'s votes.
     * @return a list of {@link VoteDto} for the {@code User}
     */
    @GET
    @Secured
    List<VoteDto> findMyVotes();

    /**
     * Secured operation for a {@code User} to find the result for a ballot.
     *
     * @param ballotId for which to find vote results
     * @return a list of counts per ballot choice
     * @throws UnknownBallotException the ballotId given is not known
     * @throws UserNotVotedException teh user has not voted for this ballot
     */
    @GET
    @Path("{id}")
    @Secured
    List<ChoiceCount> findBallotVotes(@PathParam("id")Integer ballotId) throws UnknownBallotException, UserNotVotedException;


}
