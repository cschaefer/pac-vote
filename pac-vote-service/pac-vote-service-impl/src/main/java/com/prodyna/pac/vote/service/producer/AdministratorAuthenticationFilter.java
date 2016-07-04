package com.prodyna.pac.vote.service.producer;

import com.prodyna.pac.vote.annotations.AdministratorSecured;
import com.prodyna.pac.vote.service.api.model.User;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

@AdministratorSecured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AdministratorAuthenticationFilter extends AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private Logger log;

    @Override
    protected void validateUser(User user) {

        if (!user.isAdministrator()) {
            this.log.error("User [{}] is not administrator", user);
            throw new NotAuthorizedException("User is not administrator");
        }

        this.log.info("User [{}] is administrator", user);

    }


}