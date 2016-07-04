package com.prodyna.pac.vote.service.api.model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

public class BallotTest {
    
    @Test
    public void testBallotLastDate() {
        
        Ballot ballot = new Ballot();
        ballot.setLastVoteDate(LocalDate.now());
        
        assertEquals(ballot.getLastVoteDate(), LocalDate.now());
        
    }

}
