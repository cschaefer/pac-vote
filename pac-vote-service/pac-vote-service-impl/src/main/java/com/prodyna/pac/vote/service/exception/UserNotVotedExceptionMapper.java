package com.prodyna.pac.vote.service.exception;

import com.prodyna.pac.vote.service.exceptions.UserNotVotedException;

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
public class UserNotVotedExceptionMapper implements ExceptionMapper<UserNotVotedException> {

    @Override
    public Response toResponse(UserNotVotedException e) {
        return Response
                .status(Response.Status.PRECONDITION_FAILED)
                .entity(Entity.json(e))
                .build();
    }

}
