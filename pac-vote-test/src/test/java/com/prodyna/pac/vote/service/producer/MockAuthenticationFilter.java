package com.prodyna.pac.vote.service.producer;

import com.prodyna.pac.vote.annotations.AuthenticatedUser;
import com.prodyna.pac.vote.annotations.Secured;
import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.repo.api.UserRepository;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
@Alternative
public class MockAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private Logger log;

    @Inject
    private UserRepository userRepo;

    @Inject
    @AuthenticatedUser
    Event<Integer> userAuthenticatedEvent;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        this.log.info("Executing MockAuthenticationFilter ...");

        // Get the HTTP Authorization header from the request
        final String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("TestBearer ")) {
            this.log.warn("No authorization header found!");
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
        }

        // Extract the token from the HTTP Authorization header
        final String userName = authorizationHeader.substring("TestBearer".length()).trim();

        this.log.debug("Extracted token to validate {{}}", userName);

        final User authorizedUser = this.userRepo.findByName(userName);
        if (Objects.nonNull(authorizedUser)) {
            this.log.warn("Mock filter authenticated user [{}]", authorizedUser);
            this.userAuthenticatedEvent.fire(authorizedUser.getUserId());

        } else {
            this.log.warn("Mock filter cannot find user [{}]", userName);
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
        }

    }

}
