package com.prodyna.pac.vote.service.exceptions;

/**
 * The user cannot be authorized.
 * 
 * @author cschaefer
 *
 */
public class UnauthorizedUserException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnauthorizedUserException() {
        super();
    }
    
    public UnauthorizedUserException(String message) {
        super(message);
    }
    
}
