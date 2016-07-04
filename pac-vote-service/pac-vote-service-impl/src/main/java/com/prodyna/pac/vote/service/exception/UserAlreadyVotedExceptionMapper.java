package com.prodyna.pac.vote.service.exception;

import com.prodyna.pac.vote.service.exceptions.UserAlreadyVotedException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Map exception into JAX-RS response.
 * 
 * @author cschaefer
 *
 */
@Provider
public class UserAlreadyVotedExceptionMapper implements ExceptionMapper<UserAlreadyVotedException> {

    @Override
    public Response toResponse(UserAlreadyVotedException e) {
        return Response
                .status(Response.Status.PRECONDITION_FAILED)
                .entity(Entity.json(e))
                .build();
    }

}
