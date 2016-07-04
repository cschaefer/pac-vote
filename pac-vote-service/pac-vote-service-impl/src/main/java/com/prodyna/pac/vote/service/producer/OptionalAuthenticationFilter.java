package com.prodyna.pac.vote.service.producer;

import com.prodyna.pac.vote.annotations.OptionalSecured;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

@OptionalSecured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class OptionalAuthenticationFilter extends AbstractAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private Logger log;

    @Override
    protected String extractAuhtorizationBearer(String authorizationHeader) throws NotAuthorizedException {
        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            this.log.warn("No authorization header found!");

            // make a guest user
            super.fireAuthenticateEvent(-1);

            return null;
        }

        // Extract the token from the HTTP Authorization header
        final String token = authorizationHeader.substring("Bearer".length()).trim();

        this.log.debug("Extracted token to validate {{}}", token);

        return token;
    }
}