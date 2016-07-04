package com.prodyna.pac.vote.service.api;

import com.prodyna.pac.vote.annotations.OptionalSecured;
import com.prodyna.pac.vote.annotations.Secured;
import com.prodyna.pac.vote.service.api.model.Ballot;
import com.prodyna.pac.vote.service.exceptions.NoBallotOwnerException;
import com.prodyna.pac.vote.service.exceptions.UnauthorizedUserException;
import com.prodyna.pac.vote.service.exceptions.UnknownBallotException;
import com.prodyna.pac.vote.service.exceptions.UnknownUserException;

import java.util.List;

import javax.ejb.Local;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Ballots may be retrieved, created, updated and deleted with the {@code BallotService}.
 * Any user may retrieve ballots. Authorized user may create, update and delete
 * {@code Ballot}s.
 *
 * This is a stateless REST service.
 *
 * @author cschaefer
 *
 */
@Local
@Path("/ballot")
@Consumes("application/json")
@Produces("application/json")
public interface BallotService {

    @GET
    @OptionalSecured
    List<Ballot> findAll();

    @POST
    @Secured
    Ballot create(Ballot ballot) throws NoBallotOwnerException, UnknownUserException;

    @GET
    @Path("{id}")
    Ballot findById(@PathParam("id")Integer ballotId);

    @PUT
    @Secured
    @Path("{id}")
    Ballot update(@PathParam("id") Integer ballotId, Ballot ballot) throws UnauthorizedUserException, UnknownBallotException;

    @DELETE
    @Secured
    @Path("{id}")
    void delete(@PathParam("id") Integer ballotId) throws UnauthorizedUserException, UnknownBallotException;



}
