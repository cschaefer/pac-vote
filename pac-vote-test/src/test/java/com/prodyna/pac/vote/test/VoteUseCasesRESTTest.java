package com.prodyna.pac.vote.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.prodyna.pac.vote.service.api.BallotService;
import com.prodyna.pac.vote.service.api.UserService;
import com.prodyna.pac.vote.service.api.VoteService;
import com.prodyna.pac.vote.service.api.dto.AuthorizedUser;
import com.prodyna.pac.vote.service.api.dto.OauthUnauthorizedUser;
import com.prodyna.pac.vote.service.api.dto.VoteDto;
import com.prodyna.pac.vote.service.api.model.Ballot;
import com.prodyna.pac.vote.service.api.model.Choice;
import com.prodyna.pac.vote.service.api.model.ChoiceCount;
import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.api.model.Vote;
import com.prodyna.pac.vote.service.exceptions.InvalidChoiceException;
import com.prodyna.pac.vote.service.exceptions.NoBallotOwnerException;
import com.prodyna.pac.vote.service.exceptions.UnauthorizedUserException;
import com.prodyna.pac.vote.service.exceptions.UnknownBallotException;
import com.prodyna.pac.vote.service.exceptions.UnknownChoiceException;
import com.prodyna.pac.vote.service.exceptions.UnknownUserException;
import com.prodyna.pac.vote.service.exceptions.UserAlreadyVotedException;
import com.prodyna.pac.vote.service.exceptions.UserNotVotedException;
import com.prodyna.pac.vote.service.impl.GitHubOAuthRegisterResourceImpl;
import com.prodyna.pac.vote.service.impl.TokenServiceImpl;
import com.prodyna.pac.vote.service.impl.UserValidationServiceImpl;
import com.prodyna.pac.vote.service.producer.AuthenticationFilter;
import com.prodyna.pac.vote.service.repo.api.BallotRepository;
import com.prodyna.pac.vote.service.repo.api.UserRepository;
import com.prodyna.pac.vote.service.repo.api.VoteRepository;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotAuthorizedException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.extension.rest.client.Header;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test tests are written for order of execution. Rather than having large test cases
 * the sequence of the tests builds up the use case.
 *
 * @author cschaefer
 *
 */
@RunWith(Arquillian.class)
public class VoteUseCasesRESTTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //@Deployment(testable = false)
    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
        war.addPackages(true, "com.prodyna.pac.vote")
        .addPackages(true, "org.codehaus.jackson")
        .deleteClass(TokenServiceImpl.class)
        .deleteClass(UserValidationServiceImpl.class)
        .deleteClass(GitHubOAuthRegisterResourceImpl.class)
        .deleteClass(AuthenticationFilter.class)
        .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
        .addAsWebInfResource("wildflyas-ds.xml")
        .addAsWebInfResource("test-beans.xml", "beans.xml");
        System.out.println(war.toString(true));
        return war;
    }

    @ArquillianResource
    private URL url;

    @Inject
    BallotRepository ballotRepo;

    @Inject
    UserRepository userRepo;

    @Inject
    VoteRepository voteRepo;

    private User owner;

    private User anotherUser;

    private AuthorizedUser authorizedUser;

    @Before
    public void prepareUseCaseTests() {


        this.owner = new User();
        this.owner.setProvider("github");
        this.owner.setUid("github:1232456");
        this.owner.setUserName("cschaefer");


        this.anotherUser = new User();
        this.anotherUser.setProvider("github");
        this.anotherUser.setUid("github:654321");
        this.anotherUser.setUserName("notcschaefer");


    }

    @Test
    @InSequence(0)
    public void testStepInContainerPrepare() {
        final List<Ballot> ballots = this.ballotRepo.findAll();
        if (Objects.nonNull(ballots)) {
            for (final Ballot ballot : ballots) {
                this.ballotRepo.delete(ballot.getBallotId());
            }
        }

        final List<Vote> votes = this.voteRepo.findAll();
        if (Objects.nonNull(votes)) {
            for (final Vote vote : votes) {
                this.voteRepo.delete(vote.getVoteId());
            }
        }

        final List<User> users = this.userRepo.findAll();
        if (Objects.nonNull(users)) {
            for (final User user : users) {
                this.userRepo.delete(user.getUserId());
            }

        }
    }

    @RunAsClient
    @Test
    @InSequence(1)
    public void testStepRegisterUsers(@ArquillianResteasyResource("rest") UserService userService)
            throws UnauthorizedUserException, JsonGenerationException, JsonMappingException, IOException {

        final OauthUnauthorizedUser unknownUser = new OauthUnauthorizedUser(this.owner);

        this.authorizedUser = userService.register(unknownUser);
        assertNotNull(this.authorizedUser.getUserId());
        final ObjectMapper om = new ObjectMapper();
        final String userJSON = om.writerWithDefaultPrettyPrinter().writeValueAsString(this.authorizedUser);
        this.log.info(userJSON);


        assertNotNull(userService.register(new OauthUnauthorizedUser(this.anotherUser)));

    }

    @RunAsClient
    @Test
    @InSequence(2)
    public void testStepFindNoBallotsRest(@ArquillianResteasyResource("rest") BallotService ballotService) {

        final List<Ballot> findAll = ballotService.findAll();

        assertTrue(findAll.isEmpty());

    }

    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @Test
    @InSequence(3)
    public void testSteCreateBallotRest(
            @ArquillianResteasyResource("rest") BallotService ballotService)
                    throws JsonGenerationException,
                    JsonMappingException,
                    IOException,
                    NoBallotOwnerException,
                    UnknownUserException,
                    UnauthorizedUserException {



        final Choice choice = new Choice();
        choice.setDescription("choice description");
        choice.setName("choice name");

        final Ballot ballot = new Ballot();
        ballot.setActive(false);
        ballot.setDescription("description");
        ballot.setLastVoteDate(LocalDate.now());
        ballot.setQuestion("question");


        final Set<Choice> choices = new HashSet<>();
        choices.add(choice);
        ballot.setChoices(choices);
        ballot.setOwner(this.owner);

        final Ballot createdBallot = ballotService.create(ballot);
        final ObjectMapper om = new ObjectMapper();
        final String ballotJSON = om.writerWithDefaultPrettyPrinter().writeValueAsString(createdBallot);
        this.log.info(ballotJSON);

        assertNotNull(createdBallot);
        assertNotNull(createdBallot.getBallotId());
        assertFalse(createdBallot.getChoices().isEmpty());

    }



    /**
     * @param ballotService
     * @param userService
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     * @throws UserNotVotedException
     * @throws UnknownBallotException
     * @throws UserAlreadyVotedException
     * @throws InvalidChoiceException
     * @throws UnknownChoiceException
     * @throws UnauthorizedUserException
     * @throws NoBallotOwnerException
     * @throws UnknownUserException
     */
    @RunAsClient
    @Test
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @InSequence(4)
    public void testStepCastVoteRest(
            @ArquillianResteasyResource("rest") BallotService ballotService,
            @ArquillianResteasyResource("rest") VoteService voteService,
            @ArquillianResteasyResource("rest") UserService userService)
                    throws IOException, UnknownBallotException, UserNotVotedException, UnknownChoiceException, InvalidChoiceException, UserAlreadyVotedException, UnauthorizedUserException {

        final OauthUnauthorizedUser unknownUser = new OauthUnauthorizedUser(this.owner);

        this.authorizedUser = userService.register(unknownUser);
        assertNotNull(this.authorizedUser.getUserId());

        final List<Ballot> allBallots = ballotService.findAll();

        assertEquals(1, allBallots.size());

        final Ballot ballot = allBallots.get(0);

        final Integer ballotId = ballot.getBallotId();
        final Set<Choice> choices = ballot.getChoices();

        assertFalse(choices.isEmpty());

        Choice myChoice = null;
        for (final Choice choice : choices) {
            if (myChoice == null) {
                myChoice = choice;
                break;
            }
        }
        assertTrue(Objects.nonNull(myChoice));

        assertTrue(voteService.findMyVotes().isEmpty());

        final VoteDto voteDto = new VoteDto();
        voteDto.setBallotId(ballotId);
        voteDto.setChoiceId(myChoice.getChoiceId());

        final Vote vote = voteService.vote(voteDto);

        assertEquals(ballot.getBallotId(), vote.getBallotId());
        assertEquals(myChoice.getChoiceId(), vote.getChoiceId());
        assertEquals(this.authorizedUser.getUserId(), vote.getUserId());

    }

    @RunAsClient
    @Test
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @InSequence(5)
    public void testStepFindMyVotesRest(
            @ArquillianResteasyResource("rest") BallotService ballotService,
            @ArquillianResteasyResource("rest") VoteService voteService)
                    throws IOException {


        final List<VoteDto> findMyVotes = voteService.findMyVotes();

        assertNotNull(findMyVotes);
        assertEquals(1, findMyVotes.size());

        final VoteDto voteDto = findMyVotes.get(0);

        final List<Ballot> allBallots = ballotService.findAll();

        assertEquals(1, allBallots.size());

        final Ballot ballot = allBallots.get(0);

        assertEquals(ballot.getBallotId(), voteDto.getBallotId());

        final Set<Choice> choices = ballot.getChoices();

        assertFalse(choices.isEmpty());

        Choice myChoice = null;
        for (final Choice choice : choices) {
            if (myChoice == null) {
                myChoice = choice;
                break;
            }
        }
        assertTrue(Objects.nonNull(myChoice));

        assertEquals(myChoice.getChoiceId(), voteDto.getChoiceId());


    }


    @RunAsClient
    @Test
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @InSequence(6)
    public void testStepFindVoteResultsRest(
            @ArquillianResteasyResource("rest") BallotService ballotService,
            @ArquillianResteasyResource("rest") VoteService voteService)
                    throws IOException, UnknownBallotException, UserNotVotedException {

        final List<VoteDto> findMyVotes = voteService.findMyVotes();

        assertNotNull(findMyVotes);
        assertEquals(1, findMyVotes.size());

        final VoteDto voteDto = findMyVotes.get(0);

        final List<Ballot> allBallots = ballotService.findAll();

        assertEquals(1, allBallots.size());

        final Ballot ballot = allBallots.get(0);

        final List<ChoiceCount> findBallotVotes = voteService.findBallotVotes(ballot.getBallotId());

        assertNotNull(findBallotVotes);
        assertFalse(findBallotVotes.isEmpty());

        final ChoiceCount choiceCount = findBallotVotes.get(0);
        assertEquals(Long.valueOf(1L), choiceCount.getCount());
        assertEquals(voteDto.getChoiceId(), choiceCount.getChoiceId());

    }

    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @InSequence(7)
    @Test(expected = ClientErrorException.class)
    public void testStepFailVotingAgain(
            @ArquillianResteasyResource("rest") BallotService ballotService,
            @ArquillianResteasyResource("rest") VoteService voteService)
                    throws IOException, UnknownBallotException, UnknownChoiceException, InvalidChoiceException, UserAlreadyVotedException {


        final List<VoteDto> findMyVotes = voteService.findMyVotes();

        assertNotNull(findMyVotes);
        assertEquals(1, findMyVotes.size());

        final VoteDto duplicateVote = findMyVotes.get(0);
        voteService.vote(duplicateVote);



    }

    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @InSequence(8)
    @Test(expected = ClientErrorException.class)
    public void testStepFailVotingInvalidChoice(
            @ArquillianResteasyResource("rest") BallotService ballotService,
            @ArquillianResteasyResource("rest") VoteService voteService)
                    throws IOException, UnknownBallotException, UnknownChoiceException, InvalidChoiceException, UserAlreadyVotedException {


        final List<VoteDto> findMyVotes = voteService.findMyVotes();

        assertNotNull(findMyVotes);
        assertEquals(1, findMyVotes.size());

        final VoteDto unknownChoiceVote = findMyVotes.get(0);
        unknownChoiceVote.setChoiceId(9999);
        voteService.vote(unknownChoiceVote);


    }


    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @InSequence(9)
    @Test(expected = ClientErrorException.class)
    public void testStepFailVotingUnknownChoice(
            @ArquillianResteasyResource("rest") BallotService ballotService,
            @ArquillianResteasyResource("rest") VoteService voteService)
                    throws IOException, UnknownBallotException, UnknownChoiceException, InvalidChoiceException, UserAlreadyVotedException {


        final List<VoteDto> findMyVotes = voteService.findMyVotes();

        assertNotNull(findMyVotes);
        assertEquals(1, findMyVotes.size());

        final VoteDto unknownChoiceVote = findMyVotes.get(0);
        unknownChoiceVote.setChoiceId(null);
        voteService.vote(unknownChoiceVote);


    }

    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @InSequence(10)
    @Test(expected = ClientErrorException.class)
    public void testStepFailVotingUnknownBallot(
            @ArquillianResteasyResource("rest") BallotService ballotService,
            @ArquillianResteasyResource("rest") VoteService voteService)
                    throws IOException, UnknownBallotException, UnknownChoiceException, InvalidChoiceException, UserAlreadyVotedException {


        final List<VoteDto> findMyVotes = voteService.findMyVotes();

        assertNotNull(findMyVotes);
        assertEquals(1, findMyVotes.size());

        final VoteDto unknownBallotVote = findMyVotes.get(0);
        unknownBallotVote.setBallotId(9999);
        voteService.vote(unknownBallotVote);


    }

    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer illegaltoken")
    @InSequence(11)
    @Test(expected = NotAuthorizedException.class)
    public void testStepFailVotingUnauthorizedFindMyVotes(
            @ArquillianResteasyResource("rest") BallotService ballotService,
            @ArquillianResteasyResource("rest") VoteService voteService)
                    throws IOException, UnknownBallotException, UnknownChoiceException, InvalidChoiceException, UserAlreadyVotedException {


        voteService.findMyVotes();

    }

    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer illegaltoken")
    @InSequence(12)
    @Test(expected = NotAuthorizedException.class)
    public void testStepFailVotingUnauthorizedVote(
            @ArquillianResteasyResource("rest") BallotService ballotService,
            @ArquillianResteasyResource("rest") VoteService voteService)
                    throws IOException, UnknownBallotException, UnknownChoiceException, InvalidChoiceException, UserAlreadyVotedException {


        final VoteDto voteDto = new VoteDto();
        voteDto.setBallotId(1);
        voteDto.setChoiceId(1);
        voteService.vote(voteDto);

    }


}
