package com.prodyna.pac.vote.service.api.dto;

import com.prodyna.pac.vote.service.api.model.User;

/**
 * 
 * An authenticated user from a 3rd party OAuth provider.  The user must
 * pass on their OAuth accessToken to be validated.  The applicaiton shares
 * a client ID and secret with the 3rd party OAuth provider. With
 * this information the application can validate whether the accessToken
 * was issued for this service.
 * 
 * @author cschaefer
 *
 */
public class OauthUnauthorizedUser extends User {

    private static final long serialVersionUID = 5964255533664995195L;
    
    /** 3rd Party OAuth accessToken to be validated. */
    private String authToken;

    public OauthUnauthorizedUser() {}
    
    public OauthUnauthorizedUser(User user) {
        super.setAdministrator(user.isAdministrator());
        super.setProfileImageUrl(user.getProfileImageUrl());
        super.setProvider(user.getProvider());
        super.setUid(user.getUid());
        super.setUserId(user.getUserId());
        super.setUserName(user.getUserName());
    }
    
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((authToken == null) ? 0 : authToken.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OauthUnauthorizedUser other = (OauthUnauthorizedUser) obj;
        if (authToken == null) {
            if (other.authToken != null) {
                return false;
            }
        } else if (!authToken.equals(other.authToken)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AuthorizedUser [authToken=");
        builder.append(authToken);
        builder.append(", getUserId()=");
        builder.append(getUserId());
        builder.append(", getUserName()=");
        builder.append(getUserName());
        builder.append(", getUid()=");
        builder.append(getUid());
        builder.append(", getProvider()=");
        builder.append(getProvider());
        builder.append(", getProfileImageUrl()=");
        builder.append(getProfileImageUrl());
        builder.append(", isAdministrator()=");
        builder.append(isAdministrator());
        builder.append(", hashCode()=");
        builder.append(hashCode());
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append(", getClass()=");
        builder.append(getClass());
        builder.append("]");
        return builder.toString();
    }

    
    
}
