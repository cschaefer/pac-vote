package com.prodyna.pac.vote.service.exception;

import com.prodyna.pac.vote.service.exceptions.UnauthorizedUserException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author cschaefer
 *
 */
@Provider
public class UnauthorizedUserExceptionMapper implements ExceptionMapper<UnauthorizedUserException> {

    @Override
    public Response toResponse(UnauthorizedUserException e) {
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(Entity.json(e))
                .build();
    }

}
