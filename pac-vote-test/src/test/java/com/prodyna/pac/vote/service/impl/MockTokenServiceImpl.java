package com.prodyna.pac.vote.service.impl;

import com.prodyna.pac.vote.service.api.TokenService;
import com.prodyna.pac.vote.service.api.model.User;

import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;

@Alternative
@Stateless
public class MockTokenServiceImpl implements TokenService {

    
    @Override
    public String issueToken(User user)  {
        return user.getUid();
    }

    @Override
    public User getAuthorizedUser(String jwt) {
        
        return null;
    }
    

}
