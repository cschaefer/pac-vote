package com.prodyna.pac.vote.service.exceptions;

/**
 * The user is unknown.
 * 
 * @author cschaefer
 *
 */
public class UnknownUserException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnknownUserException() {
        super();
    }
    
    public UnknownUserException(String message) {
        super(message);
    }
    
}
