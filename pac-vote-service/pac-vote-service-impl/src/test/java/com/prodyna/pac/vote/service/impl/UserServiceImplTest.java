package com.prodyna.pac.vote.service.impl;

import static org.junit.Assert.assertNotNull;

import java.security.Key;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import org.junit.Test;

public class UserServiceImplTest {

    @Test
    public void testUserToken() {
        
        
     // We need a signing key, so we'll create one just for this example. Usually
     // the key would be read from your application configuration instead.
     Key key = MacProvider.generateKey();
     key.getEncoded();
     
     String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());     

     assertNotNull(encodedKey);
     
     System.out.println(encodedKey);
     
     
     // decode the base64 encoded string
     byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
     // rebuild key using SecretKeySpec
     String alg = SignatureAlgorithm.HS512.getJcaName();
     SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, alg); 

     
    }
    
    
    
}
