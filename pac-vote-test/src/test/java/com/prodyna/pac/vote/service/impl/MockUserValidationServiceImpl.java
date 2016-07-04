package com.prodyna.pac.vote.service.impl;

import com.prodyna.pac.vote.service.api.UserValidationService;
import com.prodyna.pac.vote.service.api.dto.OauthUnauthorizedUser;

import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.slf4j.Logger;

@Alternative
@Stateless
public class MockUserValidationServiceImpl implements UserValidationService {

    @Inject
    private Logger log;

    
    @Override
    public boolean validateUser(OauthUnauthorizedUser unknownUser) {
        log.warn("For testing the OAuth user is always valid");
        return true;
    }

}
