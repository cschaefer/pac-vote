package com.prodyna.pac.vote.service.producer;

import com.prodyna.pac.vote.annotations.AuthenticatedUser;
import com.prodyna.pac.vote.service.api.TokenService;
import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.repo.api.UserRepository;

import java.io.IOException;
import java.util.Objects;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

public abstract class AbstractAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private Logger log;

    @Inject
    private TokenService tokenService;

    @Inject
    private UserRepository userRepo;

    @Inject
    @AuthenticatedUser
    Event<Integer> userAuthenticatedEvent;

    /**
     * Will extract the authorization header. The implementation must throw
     * an exception depending on its intention.  An excpetion will cause
     * the service call to halt with unauthorization.
     *
     * Should a token be returned by {@link #extractAuhtorizationBearer(String)}
     * then it will be validated.  Any token must be valid to continue
     * or otherwise the service call will be halted with unauthorized.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        this.log.info("Executing AuthenticationFilter ...");

        // Get the HTTP Authorization header from the request
        final String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        final String token = this.extractAuhtorizationBearer(authorizationHeader);

        this.log.debug("Extracted token to validate {{}}", token);

        if (Objects.nonNull(token)) {
            try {
                // Validate the token
                this.validateToken(token);

            } catch (final Exception e) {
                requestContext.abortWith(
                        Response.status(Response.Status.UNAUTHORIZED).build());
            }
        }

    }

    /**
     * If there is a token present it must be valid. A token is invalid
     * when no user can be decoded from the token or when the user
     * is decoded but cannot be found in the user repository.
     *
     * @param token to be validated
     * @throws Exception
     */
    protected void validateToken(String token) throws Exception {

        final User authorizedUser = this.tokenService.getAuthorizedUser(token);

        if (Objects.nonNull(authorizedUser)) {
            this.log.debug("Decrypted from token {{}}", authorizedUser);
            final User repoUser = this.userRepo.findByName(authorizedUser.getUserName());
            if (Objects.nonNull(repoUser)) {

                this.validateUser(repoUser);

                this.log.info("User authorized {{}}", authorizedUser);
                this.fireAuthenticateEvent(authorizedUser.getUserId());
                return;
            }
            this.log.debug("No user found as specified in token");
        }
        this.log.error("No user fround for auth token");
        throw new NotAuthorizedException("User not found");
    }

    protected void fireAuthenticateEvent(Integer userId) {
        this.userAuthenticatedEvent.fire(userId);
    }


    /**
     * Extract the authoriation token
     *
     * @param authorizationHeader to process
     * @return the authorization token or null
     * @throws NotAuthorizedException optionally throw depending on header and filter
     */
    protected abstract String extractAuhtorizationBearer(String authorizationHeader) throws NotAuthorizedException;

    /**
     * Optional hook for implementation to validate the user if found.
     *
     * @param authorizedUser
     */
    protected void validateUser(User authorizedUser) throws NotAuthorizedException {
        // intentionally not implementation
    }
}
