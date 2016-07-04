package com.prodyna.pac.vote.service.repo.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.prodyna.pac.vote.service.api.model.Ballot;
import com.prodyna.pac.vote.service.api.model.Choice;
import com.prodyna.pac.vote.service.api.model.ChoiceCount;
import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.api.model.Vote;
import com.prodyna.pac.vote.service.producer.EntityManagerProducer;
import com.prodyna.pac.vote.service.producer.LoggerProducer;
import com.prodyna.pac.vote.service.repo.api.BallotRepository;
import com.prodyna.pac.vote.service.repo.api.UserRepository;
import com.prodyna.pac.vote.service.repo.api.VoteRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class VoteRepositoryTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "com.prodyna.pac.vote.service.api")
                .addPackages(true, "com.prodyna.pac.vote.service.exceptions")
                .addPackages(true, "com.prodyna.pac.vote.service.repo")
                .addClass(LoggerProducer.class)
                .addClass(EntityManagerProducer.class)
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("wildflyas-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    BallotRepository ballotRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    VoteRepository voteRepository;

    @Before
    public void createBallot() throws Exception {

        User voter = new User();
        voter.setUserName("voter");
        voter = this.userRepository.create(voter);

        User voter2 = new User();
        voter2.setUserName("voter2");
        voter2 = this.userRepository.create(voter2);


        User owner = new User();
        owner.setUserName("owner");
        owner = this.userRepository.create(owner);

        this.createBallot(owner, "B1");
    }

    protected Ballot createBallot(User owner, String prefix) {
        final Choice choice1 = new Choice();
        choice1.setDescription(prefix + ".choice1.description");
        choice1.setName(prefix + ".choice1.name");

        final Choice choice2 = new Choice();
        choice2.setDescription(prefix + ".choice2.description");
        choice2.setName(prefix + ".choice2.name");

        final Set<Choice> choices = new HashSet<>();
        choices.add(choice1);
        choices.add(choice2);

        final Ballot ballot = new Ballot();
        ballot.setOwner(owner);
        ballot.setActive(false);
        ballot.setDescription(prefix + ".description");
        ballot.setLastVoteDate(LocalDate.now());
        ballot.setQuestion(prefix + ".question");
        ballot.setChoices(choices);

        final Ballot createdBallot = this.ballotRepository.create(ballot);
        assertNotNull(createdBallot.getBallotId());
        assertEquals(createdBallot.getLastVoteDate(), LocalDate.now());

        return createdBallot;

    }

    @After
    public void clearData() throws Exception {
        final List<Vote> votes = this.voteRepository.findAll();
        for (final Vote vote : votes) {
            this.voteRepository.delete(vote.getVoteId());
        }

        final List<Ballot> ballots = this.ballotRepository.findAll();
        for (final Ballot ballot : ballots) {
            this.ballotRepository.delete(ballot.getBallotId());
        }

        final List<User> users = this.userRepository.findAll();
        for (final User user : users) {
            this.userRepository.delete(user.getUserId());
        }


    }


    @Test
    public void castVote() {

        final User voter = this.userRepository.findByName("voter");

        final List<Ballot> allBallots = this.ballotRepository.findAll();

        assertNotNull(allBallots);
        assertFalse(allBallots.isEmpty());
        assertEquals(1, allBallots.size());

        final Ballot ballot = allBallots.get(0);

        assertNotNull(ballot.getChoices());
        assertEquals(2, ballot.getChoices().size());

        Choice voterChoice = null;
        for (final Choice choice :ballot.getChoices()) {
            if ("B1.choice1.name".equals(choice.getName())) {
                voterChoice = choice;
            }
        }

        final Vote vote = new Vote();
        vote.setBallotId(ballot.getBallotId());
        vote.setChoiceId(voterChoice.getChoiceId());
        vote.setUserId(voter.getUserId());

        final Vote createdVote = this.voteRepository.create(vote);

        assertNotNull(createdVote);
        assertNotNull(createdVote.getVoteId());
    }

    @Test
    public void findVote() {

        final User voter = this.userRepository.findByName("voter");

        final List<Ballot> allBallots = this.ballotRepository.findAll();

        assertNotNull(allBallots);
        assertFalse(allBallots.isEmpty());
        assertEquals(1, allBallots.size());

        final Ballot ballot = allBallots.get(0);

        assertNotNull(ballot.getChoices());
        assertEquals(2, ballot.getChoices().size());

        Choice voterChoice = null;
        for (final Choice choice :ballot.getChoices()) {
            if ("B1.choice1.name".equals(choice.getName())) {
                voterChoice = choice;
            }
        }
        assertNotNull(voterChoice);

        final Vote vote = new Vote();
        vote.setBallotId(ballot.getBallotId());
        vote.setChoiceId(voterChoice.getChoiceId());
        vote.setUserId(voter.getUserId());

        final Vote createdVote = this.voteRepository.create(vote);

        assertNotNull(createdVote);
        assertNotNull(createdVote.getVoteId());
        assertNotNull(createdVote.getBallotId());
        assertNotNull(createdVote.getChoiceId());

        final Vote foundVote = this.voteRepository.findById(createdVote.getVoteId());
        assertEquals(createdVote, foundVote);

    }

    @Test
    public void findBallotResults() {

        final User voter = this.userRepository.findByName("voter");

        final List<Ballot> allBallots = this.ballotRepository.findAll();

        assertNotNull(allBallots);
        assertFalse(allBallots.isEmpty());
        assertEquals(1, allBallots.size());

        final Ballot ballot = allBallots.get(0);

        assertNotNull(ballot.getChoices());
        assertEquals(2, ballot.getChoices().size());

        Choice voterChoice = null;
        for (final Choice choice :ballot.getChoices()) {
            if ("B1.choice1.name".equals(choice.getName())) {
                voterChoice = choice;
            }
        }

        final Vote vote = new Vote();
        vote.setBallotId(ballot.getBallotId());
        vote.setChoiceId(voterChoice.getChoiceId());
        vote.setUserId(voter.getUserId());

        final Vote createdVote = this.voteRepository.create(vote);

        assertNotNull(createdVote);
        assertNotNull(createdVote.getVoteId());

        final Vote foundVote = this.voteRepository.findById(createdVote.getVoteId());
        assertEquals(createdVote, foundVote);

        final List<Vote> findBallotResults = this.voteRepository.findBallotVotes(ballot.getBallotId());
        assertNotNull(findBallotResults);
        assertEquals(1, findBallotResults.size());
        assertEquals(foundVote, findBallotResults.get(0));

    }


    @Test
    public void findVoteResult() {

        final User voter = this.userRepository.findByName("voter");

        final List<Ballot> allBallots = this.ballotRepository.findAll();

        assertNotNull(allBallots);
        assertFalse(allBallots.isEmpty());
        assertEquals(1, allBallots.size());

        final Ballot ballot = allBallots.get(0);

        assertNotNull(ballot.getChoices());
        assertEquals(2, ballot.getChoices().size());

        Choice voterChoice = null;
        for (final Choice choice :ballot.getChoices()) {
            if ("B1.choice1.name".equals(choice.getName())) {
                voterChoice = choice;
            }
        }
        assertNotNull(voterChoice);

        final Vote vote = new Vote();
        vote.setBallotId(ballot.getBallotId());
        vote.setChoiceId(voterChoice.getChoiceId());
        vote.setUserId(vote.getUserId());

        final Vote createdVote = this.voteRepository.create(vote);

        assertNotNull(createdVote);
        assertNotNull(createdVote.getVoteId());
        assertNotNull(createdVote.getBallotId());
        assertNotNull(createdVote.getChoiceId());

        final Vote foundVote = this.voteRepository.findById(createdVote.getVoteId());
        assertEquals(createdVote, foundVote);

        final List<ChoiceCount> findBallotResult = this.voteRepository.findBallotResult(ballot.getBallotId());
        assertNotNull(findBallotResult);
        assertEquals(1, findBallotResult.size());
        assertEquals(Long.valueOf(1), findBallotResult.get(0).getCount());



    }

    @Test
    public void findVoteResult2() {

        final User voter = this.userRepository.findByName("voter");
        final User voter2 = this.userRepository.findByName("voter2");

        final List<Ballot> allBallots = this.ballotRepository.findAll();

        assertNotNull(allBallots);
        assertFalse(allBallots.isEmpty());
        assertEquals(1, allBallots.size());

        final Ballot ballot = allBallots.get(0);

        assertNotNull(ballot.getChoices());
        assertEquals(2, ballot.getChoices().size());

        Choice voterChoice = null;
        for (final Choice choice :ballot.getChoices()) {
            if ("B1.choice1.name".equals(choice.getName())) {
                voterChoice = choice;
            }
        }

        final Vote vote = new Vote();
        vote.setBallotId(ballot.getBallotId());
        vote.setChoiceId(voterChoice.getChoiceId());
        vote.setUserId(voter.getUserId());

        final Vote createdVote = this.voteRepository.create(vote);

        assertNotNull(createdVote);
        assertNotNull(createdVote.getVoteId());

        final Vote foundVote = this.voteRepository.findById(createdVote.getVoteId());
        assertEquals(createdVote, foundVote);


        Choice voter2Choice = null;
        for (final Choice choice :ballot.getChoices()) {
            if ("B1.choice2.name".equals(choice.getName())) {
                voter2Choice = choice;
            }
        }


        final Vote vote2 = new Vote();
        vote2.setBallotId(ballot.getBallotId());
        vote2.setChoiceId(voter2Choice.getChoiceId());
        vote2.setUserId(voter2.getUserId());

        final Vote createdVote2 = this.voteRepository.create(vote2);

        assertNotNull(createdVote2);
        assertNotNull(createdVote2.getVoteId());

        final Vote foundVote2 = this.voteRepository.findById(createdVote2.getVoteId());
        assertEquals(createdVote2, foundVote2);



        final List<ChoiceCount> findBallotResult = this.voteRepository.findBallotResult(ballot.getBallotId());
        System.out.println(findBallotResult);
        assertNotNull(findBallotResult);
        assertEquals(2, findBallotResult.size());



    }



    @Test
    public void findMyVotes() {

        final User voter = this.userRepository.findByName("voter");

        this.createBallot(voter, "B2");
        this.createBallot(voter, "B3");

        final List<Ballot> allBallots = this.ballotRepository.findAll();

        assertNotNull(allBallots);
        assertFalse(allBallots.isEmpty());
        assertEquals(3, allBallots.size());

        final List<Vote> createdVotes = new ArrayList<Vote>();

        for (final Ballot ballot : allBallots) {

            assertNotNull(ballot.getChoices());
            assertEquals(2, ballot.getChoices().size());

            Choice voterChoice = null;
            for (final Choice choice :ballot.getChoices()) {
                if (choice.getName().endsWith("choice1.name")) {
                    voterChoice = choice;
                }
            }

            final Vote vote = new Vote();
            vote.setBallotId(ballot.getBallotId());
            vote.setChoiceId(voterChoice.getChoiceId());
            vote.setUserId(voter.getUserId());

            final Vote createdVote = this.voteRepository.create(vote);
            assertNotNull(createdVote);
            assertNotNull(createdVote.getVoteId());

            final Vote foundVote = this.voteRepository.findById(createdVote.getVoteId());
            assertEquals(createdVote, foundVote);
            createdVotes.add(createdVote);
        }


        final List<Vote> findMyBallotVotes = this.voteRepository.findMyBallotVotes(voter.getUserId());
        assertEquals(3, findMyBallotVotes.size());
        assertEquals(createdVotes, findMyBallotVotes);

    }

    @Test
    public void findMyVotes2() {

        final User voter = this.userRepository.findByName("voter");

        final Ballot b2Ballot = this.createBallot(voter, "B2");
        this.createBallot(voter, "B3");

        final List<Ballot> allBallots = this.ballotRepository.findAll();

        assertNotNull(allBallots);
        assertFalse(allBallots.isEmpty());
        assertEquals(3, allBallots.size());


        assertNotNull(b2Ballot.getChoices());
        assertEquals(2, b2Ballot.getChoices().size());

        Choice voterChoice = null;
        for (final Choice choice : b2Ballot.getChoices()) {
            if (choice.getName().endsWith("choice1.name")) {
                voterChoice = choice;
            }
        }

        final Vote vote = new Vote();
        vote.setBallotId(b2Ballot.getBallotId());
        vote.setChoiceId(voterChoice.getChoiceId());
        vote.setUserId(voter.getUserId());

        final Vote createdVote = this.voteRepository.create(vote);
        assertNotNull(createdVote);
        assertNotNull(createdVote.getVoteId());

        final List<Vote> findMyBallotVotes = this.voteRepository.findMyBallotVotes(voter.getUserId());
        assertEquals(1, findMyBallotVotes.size());
        assertEquals(b2Ballot.getBallotId(), findMyBallotVotes.get(0).getBallotId());
        assertEquals(voterChoice.getChoiceId(), findMyBallotVotes.get(0).getChoiceId());

    }

    @Test
    public void findMyVotes3() {

        final User voter = this.userRepository.findByName("voter");

        final Ballot b2Ballot = this.createBallot(voter, "B2");
        this.createBallot(voter, "B3");

        final List<Ballot> allBallots = this.ballotRepository.findAll();

        assertNotNull(allBallots);
        assertFalse(allBallots.isEmpty());
        assertEquals(3, allBallots.size());


        assertNotNull(b2Ballot.getChoices());
        assertEquals(2, b2Ballot.getChoices().size());

        Choice voterChoice = null;
        for (final Choice choice : b2Ballot.getChoices()) {
            if (choice.getName().endsWith("choice1.name")) {
                voterChoice = choice;
            }
        }

        final Vote vote = new Vote();
        vote.setBallotId(b2Ballot.getBallotId());
        vote.setChoiceId(voterChoice.getChoiceId());
        vote.setUserId(voter.getUserId());
        vote.setVoteDate(LocalDate.now());

        final Vote createdVote = this.voteRepository.create(vote);
        assertNotNull(createdVote);
        assertNotNull(createdVote.getVoteId());

        final List<Vote> findMyBallotVotes = this.voteRepository.findMyBallotVotes(voter.getUserId());
        assertEquals(1, findMyBallotVotes.size());
        assertEquals(b2Ballot.getBallotId(), findMyBallotVotes.get(0).getBallotId());
        assertEquals(voterChoice.getChoiceId(), findMyBallotVotes.get(0).getChoiceId());

    }


}
