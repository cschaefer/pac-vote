package com.prodyna.pac.vote.service.exceptions;

/**
 * The choice is not valid.
 * 
 * @author cschaefer
 *
 */
public class InvalidChoiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidChoiceException() {
        super();
    }
    
    public InvalidChoiceException(String message) {
        super(message);
    }
    
}
