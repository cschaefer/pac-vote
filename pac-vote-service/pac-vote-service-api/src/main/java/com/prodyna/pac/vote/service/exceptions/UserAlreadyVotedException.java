package com.prodyna.pac.vote.service.exceptions;

/**
 * The user has already voted.
 * 
 * @author cschaefer
 *
 */
public class UserAlreadyVotedException extends Exception {

    private static final long serialVersionUID = 1L;

    public UserAlreadyVotedException() {
        super();
    }
    
    public UserAlreadyVotedException(String message) {
        super(message);
    }
    
}
