package com.prodyna.pac.vote.service.api;

import com.prodyna.pac.vote.service.api.dto.OauthUnauthorizedUser;

import javax.ejb.Local;

/**
 * Internal service to validate OAuth accessTokens issued to a user.
 * 
 * @author cschaefer
 *
 */
@Local
public interface UserValidationService {

    /**
     * Determine if the given OAuth user is valid by checking their OAuth token
     * against the service which we know.
     * 
     * @param unknownUser
     * @return true if the OAuth provider acknowledges it issued the OAuth token
     *         for the known client key and secret
     */
    boolean validateUser(OauthUnauthorizedUser unknownUser);

}
