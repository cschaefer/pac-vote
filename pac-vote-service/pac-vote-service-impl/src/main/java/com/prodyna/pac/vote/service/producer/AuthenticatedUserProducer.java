package com.prodyna.pac.vote.service.producer;

import com.prodyna.pac.vote.annotations.AuthenticatedUser;
import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.repo.api.UserRepository;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@RequestScoped
public class AuthenticatedUserProducer {

    @Produces
    @RequestScoped
    @AuthenticatedUser
    private User authenticatedUser;

    @Inject
    private UserRepository userRepo;

    public void handleAuthenticationEvent(@Observes @AuthenticatedUser Integer userId) {
        if (userId == -1) {
            final User guest = new User();
            guest.setAdministrator(false);
            guest.setUserName("guest");
            guest.setUserId(userId);
            this.authenticatedUser =  guest;
        } else {
            this.authenticatedUser = this.findUser(userId);
        }
    }

    private User findUser(Integer userId) {
        return this.userRepo.findById(userId);
    }
}