package com.prodyna.pac.vote.service.api.model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class VoteTest {

    @Test
    public void testEquals() {
        final User user1 = new User();
        user1.setUserName("voter");
        user1.setUserId(1);

        final Ballot ballot1 = new Ballot();
        ballot1.setBallotId(4);
        ballot1.setQuestion("question");
        ballot1.setDescription("description");
        ballot1.setLastVoteDate(LocalDate.now());

        final Choice choice1 = new Choice();
        choice1.setChoiceId(1);
        choice1.setDescription("description");
        choice1.setName("name");
        final Choice choice1a = new Choice();
        choice1a.setChoiceId(2);
        choice1a.setDescription("description");
        choice1a.setName("name");
        final Set<Choice> choices = new HashSet<Choice>();
        choices.add(choice1);
        choices.add(choice1a);
        ballot1.setChoices(choices);

        final Vote vote1 = new Vote();
        vote1.setBallotId(ballot1.getBallotId());
        vote1.setChoiceId(choice1.getChoiceId());
        vote1.setUserId(user1.getUserId());
        vote1.setVoteId(1);

        final User user2 = new User();
        user2.setUserName("voter");
        user2.setUserId(1);

        final Ballot ballot2 = new Ballot();
        ballot2.setBallotId(4);
        ballot2.setQuestion("question");
        ballot2.setDescription("description");
        ballot2.setLastVoteDate(LocalDate.now());

        final Choice choice2 = new Choice();
        choice2.setBallotId(4);
        choice2.setChoiceId(1);
        choice2.setDescription("description");
        choice2.setName("name");
        final Choice choice2a = new Choice();
        choice2a.setChoiceId(2);
        choice2a.setDescription("description");
        choice2a.setName("name");
        final Set<Choice> choices2 = new HashSet<Choice>();
        choices2.add(choice2);
        choices2.add(choice2a);
        ballot2.setChoices(choices2);

        final Vote vote2 = new Vote();
        vote2.setBallotId(ballot2.getBallotId());
        vote2.setChoiceId(choice2.getChoiceId());
        vote2.setUserId(user2.getUserId());
        vote2.setVoteId(1);

        assertEquals(vote1, vote2);

    }

}
