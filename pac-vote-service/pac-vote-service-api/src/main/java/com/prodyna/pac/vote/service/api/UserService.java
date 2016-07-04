package com.prodyna.pac.vote.service.api;

import com.prodyna.pac.vote.annotations.AdministratorSecured;
import com.prodyna.pac.vote.service.api.dto.AuthorizedUser;
import com.prodyna.pac.vote.service.api.dto.OauthUnauthorizedUser;
import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.exceptions.UnauthorizedUserException;
import com.prodyna.pac.vote.service.exceptions.UnknownUserException;

import java.util.List;

import javax.ejb.Local;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * The {@code UserService} allows OAuth authenticated users to be registered
 * with this application if the application can validate the {@link OauthUnauthorizedUser}'s
 * accessToken.
 *
 *
 * @author cschaefer
 *
 */
@Local
@Path("/user")
@Consumes("application/json")
@Produces("application/json")
public interface UserService {

    /**
     * Registers a {@link User} should the application validate the OAuth access token.
     *
     * @param user OAuth authenticated user to be authorized
     * @return an application {@link AuthorizedUser} with an authToken for this app
     * @throws UnauthorizedUserException when the OAuth accessToken is invalid
     */
    @POST
    AuthorizedUser register(OauthUnauthorizedUser user) throws UnauthorizedUserException;

    /**
     * Allow administrators to enable or disable other administrators.
     *
     * @param userId
     * @return
     * @throws UnknownUserException when user not found
     */
    @PUT
    @Path("/administrator/{id}")
    @AdministratorSecured
    User toggleAdministrator(@PathParam("id") Integer userId) throws UnknownUserException;

    /**
     * Retrieves the list of users.
     *
     * @return a list of users
     * @throws UnauthorizedUserException when requesting user not authorized.
     */
    @GET
    @AdministratorSecured
    List<User> getUsers() throws UnauthorizedUserException;
}
