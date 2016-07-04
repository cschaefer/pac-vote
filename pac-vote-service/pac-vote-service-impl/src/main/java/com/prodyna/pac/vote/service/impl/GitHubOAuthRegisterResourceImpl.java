package com.prodyna.pac.vote.service.impl;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.prodyna.pac.vote.annotations.ApplicationProperty;
import com.prodyna.pac.vote.service.api.GitHubOAuthRegisterResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

/**
 *
 * Handles OAuth authentication with GitHub.  This is managed from within the application
 * server.  This requires the application client side code to delegate all oauth activity into
 * the REST API.
 *
 * @author cschaefer
 * @deprecated see {@link UserValidationServiceImpl} for other approach with user token validation
 */
@Deprecated
public class GitHubOAuthRegisterResourceImpl implements GitHubOAuthRegisterResource {

    @Inject
    private Logger log;

    @Inject
    @ApplicationProperty(name = "com.prodyna.pac.vote.server.oauth.protected.resource.url")
    private String protectedResourceUrl;

    @Inject
    @ApplicationProperty(name = "com.prodyna.pac.vote.server.oauth.client.id")
    private String clientKey;

    @Inject
    @ApplicationProperty(name = "com.prodyna.pac.vote.server.oauth.client.secret")
    private String clientSecret;

    @Inject
    @ApplicationProperty(name = "com.prodyna.pac.vote.server.oauth.callback.url")
    private String callBackUrl;

    @Override
    public Response redirectToAuthorization() {
        final OAuth20Service service = this.createService().callback(this.callBackUrl).build(GitHubApi.instance());

        final String authorizationUrl = service.getAuthorizationUrl();

        return Response.seeOther(URI.create(authorizationUrl)).build();
    }

    @Override
    public Response callback(@QueryParam("code") String code) {
        final OAuth20Service service = this.createService().callback(this.callBackUrl).build(GitHubApi.instance());

        this.log.info("Code {}", code);

        final OAuth2AccessToken accessToken = service.getAccessToken(code);
        this.log.info("AccessToken {}", accessToken);

        this.log.info("AccessToken.id_token {}", accessToken.getParameter("id_token"));

        final OAuthRequest request = new OAuthRequest(Verb.GET, this.protectedResourceUrl, service);

        service.signRequest(accessToken, request);

        final com.github.scribejava.core.model.Response response = request.send();

        this.log.info("Response code: " + response.getCode());
        this.log.info("Response body: " + response.getBody());


        try {
            final URL url = new URL("https://"+ this.clientKey + ":" + this.clientSecret +"@api.github.com/applications/"+ this.clientKey + "/tokens/" + accessToken.getAccessToken());
            final URLConnection urlConnection = url.openConnection();

            if (url.getUserInfo() != null) {
                final String basicAuth = "Basic " + new String(Base64.getEncoder().encode(url.getUserInfo().getBytes()));
                urlConnection.setRequestProperty("Authorization", basicAuth);
            }

            final InputStream inputStream = urlConnection.getInputStream();

            final StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }

            this.log.info(textBuilder.toString());

        } catch (final MalformedURLException e) {
            this.log.error("ERROR", e);
        } catch (final IOException e) {
            this.log.error("ERROR", e);
        }

        return Response.ok(response.getBody()).build();
    }

    private ServiceBuilder createService() {
        return new ServiceBuilder()
        .debug()
        .apiKey(this.clientKey)
        .apiSecret(this.clientSecret)
        .scope("repo,user")
        .state("secret" + new Random().nextInt(999_999));
    }
}
