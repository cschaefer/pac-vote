package com.prodyna.pac.vote.service.exception;

import com.prodyna.pac.vote.service.exceptions.UnknownUserException;

import javax.ws.rs.core.MediaType;
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
public class UnknownUserExceptionMapper implements ExceptionMapper<UnknownUserException> {

    @Override
    public Response toResponse(UnknownUserException e) {
        return Response
                .status(Response.Status.PRECONDITION_FAILED)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(e.getMessage())
                .build();
    }

}
