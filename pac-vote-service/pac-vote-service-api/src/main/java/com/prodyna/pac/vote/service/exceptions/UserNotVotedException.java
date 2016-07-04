package com.prodyna.pac.vote.service.exceptions;

/**
 * The user has not voted.
 * 
 * @author cschaefer
 *
 */
public class UserNotVotedException extends Exception {

    private static final long serialVersionUID = 1L;

    public UserNotVotedException() {
        super();
    }
    
    public UserNotVotedException(String message) {
        super(message);
    }
    
}
