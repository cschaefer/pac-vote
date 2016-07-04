package com.prodyna.pac.vote.service.exceptions;

/**
 * The choice is unknown.
 * 
 * @author cschaefer
 *
 */
public class UnknownChoiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnknownChoiceException() {
        super();
    }
    
    public UnknownChoiceException(String message) {
        super(message);
    }
    
}
