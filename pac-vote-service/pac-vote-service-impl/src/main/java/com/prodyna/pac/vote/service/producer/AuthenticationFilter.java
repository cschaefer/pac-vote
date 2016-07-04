package com.prodyna.pac.vote.service.producer;

import com.prodyna.pac.vote.annotations.Secured;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter extends AbstractAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private Logger log;


    @Override
    protected String extractAuhtorizationBearer(String authorizationHeader) throws NotAuthorizedException {
        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            this.log.warn("No authorization header found!");
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        final String token = authorizationHeader.substring("Bearer".length()).trim();

        this.log.debug("Extracted token to validate {{}}", token);

        return token;
    }
}