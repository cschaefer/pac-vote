package com.prodyna.pac.vote.service.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * OAuth handler to negotiate OAuth authentication with a 3rd party
 * provider. Upon callback the implementaion exchanges the code
 * for an accessToken for the user. The callback location is known to the
 * 3rd party OAuth provider.
 * 
 * @author cschaefer
 * @deprecated
 */
@Path("github")
public interface GitHubOAuthRegisterResource {

    /**
     * Delegate control to the 3rd party OAuth provider so it can 
     * peform authentication and determine if the user wishes to
     * grant access to this application.
     * 
     * @return response
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response redirectToAuthorization();
    
    /**
     * Should the user become authenticated the 3rd party OAuth provider
     * calls back as configured for this client.  The client (this application)
     * also shares a client Id and secret and issued by teh OAuth provider.
     * 
     * @param oauthToken code issued by the OAuth provider
     * @return accessToken
     */
    @GET
    @Path("callback")
    @Produces(MediaType.TEXT_PLAIN)
    public Response callback(@QueryParam("code") String oauthToken);    
}
