package com.prodyna.pac.vote.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodyna.pac.vote.annotations.ApplicationProperty;
import com.prodyna.pac.vote.annotations.Monitored;
import com.prodyna.pac.vote.service.api.TokenService;
import com.prodyna.pac.vote.service.api.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

@Monitored
@Stateless
public class TokenServiceImpl implements TokenService {

    @Inject
    @ApplicationProperty(name = "com.prodyna.pac.vote.application.private.key")
    private String secret;

    @Inject
    private Logger log;

    @Override
    public String issueToken(User user)  {

        String jwt = null;
        final ObjectMapper om = new ObjectMapper();
        try {
            final String userJSON = om.writerWithDefaultPrettyPrinter().writeValueAsString(user);

            final String subject = userJSON;
            this.log.debug("Setting subject [{}]", subject);

            jwt = Jwts.builder()
                    .setSubject(subject)
                    .signWith(SignatureAlgorithm.HS512, this.buildKey())
                    .compact();



        } catch (final JsonProcessingException e) {
            this.log.error("Unexpected error", e);
            throw new IllegalStateException(e);
        }

        this.log.debug("Issuing token [{}]", jwt);

        return jwt;
    }

    @Override
    public User getAuthorizedUser(String jwt) {
        final String subject = Jwts.parser()
                .setSigningKey(this.buildKey())
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();

        final ObjectMapper om = new ObjectMapper();
        try {
            final User user = om.readValue(subject, User.class);
            return user;
        } catch (final JsonParseException e) {
            this.log.error("Unexpected error", e);
        } catch (final JsonMappingException e) {
            this.log.error("Unexpected error", e);
        } catch (final IOException e) {
            this.log.error("Unexpected error", e);
        }

        return null;
    }

    protected SecretKey buildKey() {
        // decode the base64 encoded string
        final byte[] decodedKey = Base64.getDecoder().decode(this.secret);
        // rebuild key using SecretKeySpec
        final String alg = SignatureAlgorithm.HS512.getJcaName();
        final SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, alg);

        return originalKey;
    }

}
