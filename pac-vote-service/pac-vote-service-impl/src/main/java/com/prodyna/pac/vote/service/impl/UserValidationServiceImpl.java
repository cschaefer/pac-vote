package com.prodyna.pac.vote.service.impl;

import com.prodyna.pac.vote.annotations.ApplicationProperty;
import com.prodyna.pac.vote.annotations.Monitored;
import com.prodyna.pac.vote.service.api.UserValidationService;
import com.prodyna.pac.vote.service.api.dto.OauthUnauthorizedUser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

/**
 *
 * @author cschaefer
 *
 */
@Monitored
@Provider
@Stateless
public class UserValidationServiceImpl implements UserValidationService {

    @Inject
    @ApplicationProperty(name = "com.prodyna.pac.vote.oauth.client.id")
    private String  clientId;

    @Inject
    @ApplicationProperty(name = "com.prodyna.pac.vote.oauth.client.secret")
    private String  clientSecret;

    @Inject
    @ApplicationProperty(name = "com.prodyna.pac.vote.oauth.github.api")
    private String  gitHubApi;

    @Inject
    private Logger log;

    @Override
    public boolean validateUser(OauthUnauthorizedUser unknownUser) {

        try {

            final String accessToken = unknownUser.getAuthToken();

            this.log.debug("User to be validated [{}]", unknownUser);

            final HttpURLConnection urlConnection = this.getConnection(accessToken);

            final OauthValidationResponse response = new OauthValidationResponse(urlConnection);

            if (response.isSuccessful()) {

                this.log.info("OAuth service validated token");

                this.log.debug("Received response [{}]", response.getBody());

                final JSONParser parse = new JSONParser();
                final JSONObject jsonObject = (JSONObject) parse.parse(response.getBody());

                final JSONObject appJSON = (JSONObject)jsonObject.get("app");
                final String clientId = (String) appJSON.get("client_id");

                final JSONObject userJSON = (JSONObject)jsonObject.get("user");
                final Long userId = (Long) userJSON.get("id");

                this.log.debug("Provider responded with clientId=[{}], userId=[{}]", clientId, userId);

                if (this.clientId.equals(clientId) && unknownUser.getUid().equals("github:"+userId)) {
                    this.log.info("User validated and registered [{}]", unknownUser);
                    return true;
                }
                this.log.warn("No matching user found for clientId [{}], userId [{}]", clientId, userId);
            } else {
                this.log.warn("Unsuccessful response from OAuth service {}", response.getBody());
            }

        } catch (final MalformedURLException e) {
            this.log.error("Unexpected error", e);
        } catch (final IOException e) {
            this.log.error("Unexpected error", e);
        } catch (final ParseException e) {
            this.log.error("Unexpected error", e);
        }

        return false;
    }


    protected HttpURLConnection getConnection(String accessToken) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(this.clientId);
        sb.append(":");
        sb.append(this.clientSecret);
        sb.append("@");
        sb.append(this.gitHubApi);
        sb.append("/");
        sb.append(this.clientId);
        sb.append("/tokens/");
        sb.append(accessToken);

        final URL url = new URL(sb.toString());
        final HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

        if (url.getUserInfo() != null) {
            final String basicAuth = "Basic " + new String(Base64.getEncoder().encode(url.getUserInfo().getBytes()));
            urlConnection.setRequestProperty("Authorization", basicAuth);
        }

        return urlConnection;
    }


}
