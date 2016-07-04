package com.prodyna.pac.vote.service.exceptions;

/**
 * The ballot is unknown.
 * 
 * @author cschaefer
 *
 */
public class UnknownBallotException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnknownBallotException() {
        super();
    }
    
    public UnknownBallotException(String message) {
        super(message);
    }
    
}
