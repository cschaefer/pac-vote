package com.prodyna.pac.vote.service.api;

import com.prodyna.pac.vote.annotations.ApplicationProperty;
import com.prodyna.pac.vote.service.api.model.User;

import javax.ejb.Local;

/**
 * An internal service which issues an authorization token
 * for the {@link User}.  The  service client must
 * retain this token and submit it in the Authorization request header
 * as follows for any secure operations.
 *
 * HTTP Header:
 * Authorization: Bearer <authoriationToken>
 *
 * Note: This is not to be confused with the OAuth access token which is
 * simply used for authentication purposes.  This service issues encoded tokens
 * using the Java Web Tokens standard. The private key is configured
 * for the application as an {@link ApplicationProperty}.
 *
 * The private key must be a HS512 512 bit secret key that is Base64 enncoded.
 *
 *
 * @see https://jwt.io/
 *  *
 *
 * @author cschaefer
 *
 */
@Local
public interface TokenService {

    /**
     * Issue an authorization token for the user.
     *
     * @param user for which to generate a token
     * @return an authorization token
     */
    String issueToken(User user);

    /**
     * Decrypt the {@link User} from the JWT token.
     *
     * @param jwt for which to decode the user
     * @return the decrypted {@link User} or null
     */
    User getAuthorizedUser(String jwt);

}
