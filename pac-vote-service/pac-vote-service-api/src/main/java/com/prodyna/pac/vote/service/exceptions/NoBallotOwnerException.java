package com.prodyna.pac.vote.service.exceptions;

/**
 * The ballot has no owner.
 * 
 * @author cschaefer
 *
 */
public class NoBallotOwnerException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoBallotOwnerException() {
        super();
    }
    
    public NoBallotOwnerException(String message) {
        super(message);
    }
    
}
