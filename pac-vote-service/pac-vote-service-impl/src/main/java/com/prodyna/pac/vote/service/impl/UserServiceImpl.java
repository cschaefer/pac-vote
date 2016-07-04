package com.prodyna.pac.vote.service.impl;

import com.prodyna.pac.vote.annotations.AuthenticatedUser;
import com.prodyna.pac.vote.annotations.Monitored;
import com.prodyna.pac.vote.service.api.TokenService;
import com.prodyna.pac.vote.service.api.UserService;
import com.prodyna.pac.vote.service.api.UserValidationService;
import com.prodyna.pac.vote.service.api.dto.AuthorizedUser;
import com.prodyna.pac.vote.service.api.dto.OauthUnauthorizedUser;
import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.exceptions.UnauthorizedUserException;
import com.prodyna.pac.vote.service.exceptions.UnknownUserException;
import com.prodyna.pac.vote.service.repo.api.UserRepository;

import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

@Monitored
@Provider
@Stateless
public class UserServiceImpl implements UserService {


    @Inject
    private Logger log;

    @Inject
    private UserRepository repo;

    @Inject
    private TokenService tokenService;

    @Inject
    private UserValidationService userValidationService;

    @Inject
    private UserRepository userRepository;

    @Inject
    @AuthenticatedUser
    private User authenticatedUser;

    @Override
    public AuthorizedUser register(OauthUnauthorizedUser unknownUser) throws UnauthorizedUserException {

        // is this user carrying a valid oauth token?
        final boolean isValidUser = this.userValidationService.validateUser(unknownUser);
        if (!isValidUser) {
            throw new UnauthorizedUserException();
        }

        User knownUser = this.repo.findByUid(unknownUser.getUid());


        if (Objects.isNull(knownUser)) {
            this.log.info("New user detected " + unknownUser);

            boolean isAdmin = false;
            final List<User> allUsers = this.repo.findAll();
            if (Objects.isNull(allUsers) || allUsers.isEmpty()) {

                this.log.info("First user detected " + unknownUser);
                this.log.info(" ... is now admin " + unknownUser);

                isAdmin = true;
            }

            final User newUser = new User();
            newUser.setAdministrator(isAdmin);
            newUser.setProfileImageUrl(unknownUser.getProfileImageUrl());
            newUser.setProvider(unknownUser.getProvider());
            newUser.setUid(unknownUser.getUid());
            newUser.setUserName(unknownUser.getUserName());
            knownUser = this.repo.create(newUser);
            this.log.info("New user created " + knownUser);
        }

        final String serviceToken = this.issueToken(knownUser);

        final AuthorizedUser authUser = new AuthorizedUser(knownUser);
        authUser.setAuthToken(serviceToken);
        return authUser;

    }

    @Override
    public User toggleAdministrator(Integer userId) throws UnknownUserException {

        if (Objects.isNull(userId)) {
            throw new UnknownUserException("User id null");
        }

        final User user = this.userRepository.findById(userId);

        if (Objects.isNull(user)) {
            throw new UnknownUserException("No user for Id");
        }

        user.setAdministrator(!user.isAdministrator());
        final User updatedUser = this.userRepository.update(user);

        return updatedUser;
    }

    @Override
    public List<User> getUsers() throws UnauthorizedUserException {

        final List<User> allUsers = this.repo.findAll();

        return allUsers;
    }

    protected String issueToken(User user) {
        return this.tokenService.issueToken(user);
    }

}
