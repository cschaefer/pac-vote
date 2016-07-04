package com.prodyna.pac.vote.service.repo.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.prodyna.pac.vote.service.api.model.Ballot;
import com.prodyna.pac.vote.service.api.model.Choice;
import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.producer.EntityManagerProducer;
import com.prodyna.pac.vote.service.producer.LoggerProducer;
import com.prodyna.pac.vote.service.repo.api.BallotRepository;
import com.prodyna.pac.vote.service.repo.api.UserRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
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
public class BallotRepositoryTest {

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

    @Before
    public void createOwner() throws Exception {

        User user = new User();
        user.setUserName("owner");
        user = this.userRepository.create(user);

    }

    @After
    public void clearData() throws Exception {
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
    public void createBallot() {

        final User user = this.userRepository.findByName("owner");

        final Ballot ballot = new Ballot();
        ballot.setOwner(user);
        ballot.setActive(true);
        ballot.setDescription("description");
        ballot.setLastVoteDate(LocalDate.now());
        ballot.setQuestion("question");
        final Ballot create = this.ballotRepository.create(ballot);

        assertNotNull(create.getBallotId());
        assertEquals(create.getLastVoteDate(), LocalDate.now());

    }

    @Test(expected = EJBTransactionRolledbackException.class)
    public void createBallotNullQuestionNOK() {

        final User user = this.userRepository.findByName("owner");

        final Ballot ballot = new Ballot();
        ballot.setOwner(user);
        ballot.setActive(true);
        ballot.setDescription("description");
        ballot.setLastVoteDate(LocalDate.now());
        ballot.setQuestion(null);
        final Ballot create = this.ballotRepository.create(ballot);

        assertNotNull(create.getBallotId());
        assertEquals(create.getLastVoteDate(), LocalDate.now());

    }

    @Test
    public void createBallotNullDescriptionOK() {

        final User user = this.userRepository.findByName("owner");

        final Ballot ballot = new Ballot();
        ballot.setOwner(user);
        ballot.setActive(true);
        ballot.setDescription(null);
        ballot.setLastVoteDate(LocalDate.now());
        ballot.setQuestion("question");
        final Ballot create = this.ballotRepository.create(ballot);

        assertNotNull(create.getBallotId());
        assertEquals(create.getLastVoteDate(), LocalDate.now());

    }

    @Test
    public void findBallot() {

        final User user = this.userRepository.findByName("owner");

        Ballot ballot = new Ballot();
        ballot.setOwner(user);
        ballot.setActive(true);
        ballot.setDescription("description");
        ballot.setLastVoteDate(LocalDate.now());
        ballot.setQuestion("question");
        ballot = this.ballotRepository.create(ballot);

        assertNotNull(ballot.getBallotId());

        final Ballot foundBallot = this.ballotRepository.findById(ballot.getBallotId());

        assertEquals(ballot, foundBallot);
    }

    @Test
    public void updateBallot() {

        final User user = this.userRepository.findByName("owner");

        Ballot ballot = new Ballot();
        ballot.setOwner(user);
        ballot.setActive(false);
        ballot.setDescription("description");
        ballot.setLastVoteDate(LocalDate.now());
        ballot.setQuestion("question");
        ballot = this.ballotRepository.create(ballot);

        assertNotNull(ballot.getBallotId());

        final Ballot foundBallot = this.ballotRepository.findById(ballot.getBallotId());

        assertEquals(ballot, foundBallot);
        assertFalse(foundBallot.isActive());

        foundBallot.setActive(true);

        final Ballot update = this.ballotRepository.update(foundBallot.getBallotId(), foundBallot);
        assertTrue(update.isActive());
    }


    @Test
    public void deleteBallot() {

        final User user = this.userRepository.findByName("owner");

        Ballot ballot = new Ballot();
        ballot.setOwner(user);
        ballot.setActive(false);
        ballot.setDescription("description");
        ballot.setLastVoteDate(LocalDate.now());
        ballot.setQuestion("question");
        ballot = this.ballotRepository.create(ballot);

        assertNotNull(ballot.getBallotId());

        final Ballot foundBallot = this.ballotRepository.findById(ballot.getBallotId());

        assertNotNull(foundBallot);
        assertEquals(ballot, foundBallot);

        this.ballotRepository.delete(foundBallot.getBallotId());

        final Ballot deletedBallot = this.ballotRepository.findById(foundBallot.getBallotId());
        assertNull(deletedBallot);

    }

    @Test(expected=EJBException.class)
    public void deleteBallotNotExists() {

        this.ballotRepository.delete(1000);

    }


    @Test
    public void createBallotWithChoices() {

        final User user = this.userRepository.findByName("owner");

        final Choice choice1 = new Choice();
        choice1.setDescription("choice1.description");
        choice1.setName("choice1.name");

        final Choice choice2 = new Choice();
        choice2.setDescription("choice2.description");
        choice2.setName("choice2.name");

        final Set<Choice> choices = new HashSet<>();
        choices.add(choice1);
        choices.add(choice2);

        final Ballot ballot = new Ballot();
        ballot.setOwner(user);
        ballot.setActive(false);
        ballot.setDescription("description");
        ballot.setLastVoteDate(LocalDate.now());
        ballot.setQuestion("question");
        ballot.setChoices(choices);

        final Ballot createdBallot = this.ballotRepository.create(ballot);

        assertNotNull(createdBallot.getBallotId());
        assertEquals(createdBallot.getLastVoteDate(), LocalDate.now());

        assertNotNull(createdBallot.getChoices());
        assertEquals(2, createdBallot.getChoices().size());

    }
}
