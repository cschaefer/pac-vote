package com.prodyna.pac.vote.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.prodyna.pac.vote.service.api.BallotService;
import com.prodyna.pac.vote.service.api.UserService;
import com.prodyna.pac.vote.service.api.dto.AuthorizedUser;
import com.prodyna.pac.vote.service.api.dto.OauthUnauthorizedUser;
import com.prodyna.pac.vote.service.api.model.Ballot;
import com.prodyna.pac.vote.service.api.model.Choice;
import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.exceptions.NoBallotOwnerException;
import com.prodyna.pac.vote.service.exceptions.UnauthorizedUserException;
import com.prodyna.pac.vote.service.exceptions.UnknownUserException;
import com.prodyna.pac.vote.service.impl.GitHubOAuthRegisterResourceImpl;
import com.prodyna.pac.vote.service.impl.TokenServiceImpl;
import com.prodyna.pac.vote.service.impl.UserValidationServiceImpl;
import com.prodyna.pac.vote.service.producer.AuthenticationFilter;
import com.prodyna.pac.vote.service.repo.api.BallotRepository;
import com.prodyna.pac.vote.service.repo.api.UserRepository;

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
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BallotUseCasesRESTTest {

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
    public void testStep000Prepare() {
        final List<Ballot> ballots = this.ballotRepo.findAll();
        if (Objects.nonNull(ballots)) {
            for (final Ballot ballot : ballots) {
                this.ballotRepo.delete(ballot.getBallotId());
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
    public void testStep001registerUsers(@ArquillianResteasyResource("rest") UserService userService)
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
    public void testStep002getAllBallotRest(@ArquillianResteasyResource("rest") BallotService ballotService) {

        final List<Ballot> findAll = ballotService.findAll();

        assertTrue(findAll.isEmpty());

    }

    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @Test
    public void testStep003createBallotRest(
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

    @RunAsClient
    @Header(name = "Authorization", value = "Bearer cschaefer")
    @Test
    public void testStep004findAllBallotRest(
            @ArquillianResteasyResource("rest") BallotService ballotService)
                    throws
                    JsonGenerationException,
                    JsonMappingException,
                    IOException,
                    NoBallotOwnerException,
                    UnknownUserException {


        final List<Ballot> findAll = ballotService.findAll();
        assertTrue(findAll.size() == 1);

    }


    /**
     * @param ballotService
     * @param userService
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     * @throws NoBallotOwnerException
     * @throws UnknownUserException
     */
    @RunAsClient
    @Test
    public void testStep005findByIdBallotRest(@ArquillianResteasyResource("rest") BallotService ballotService)
            throws JsonGenerationException, JsonMappingException, IOException, NoBallotOwnerException, UnknownUserException {

        final List<Ballot> findAll = ballotService.findAll();
        assertTrue(findAll.size() == 1);

        final Ballot foundBallot = findAll.get(0);

        final Ballot foundBallot2 = ballotService.findById(foundBallot.getBallotId());
        assertNotNull(foundBallot2);

    }

    /**
     * Checks that owner of the ballot exists.
     *
     * @param ballotService
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     * @throws NoBallotOwnerException
     * @throws UnknownUserException
     * @throws UnauthorizedUserException
     */
    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @Test(expected = ClientErrorException.class)
    public void testStep006createBallotUnknownUser(
            @ArquillianResteasyResource("rest") BallotService ballotService)
                    throws JsonGenerationException,
                    JsonMappingException,
                    IOException,
                    NoBallotOwnerException,
                    UnknownUserException,
                    UnauthorizedUserException {

        final User owner = new User();
        owner.setProvider("github");
        owner.setUid("github:654321");
        owner.setUserName("notcschaefer");

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
        ballot.setOwner(owner);

        try {
            ballotService.create(ballot);

        } catch (final Exception e) {

            System.err.println(e);
            System.err.println(e.getMessage());
            throw e;
        }
    }

    /**
     * Checks that the sender of the create request is known.
     *
     * @param ballotService
     * @param userService
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     * @throws NoBallotOwnerException
     * @throws UnknownUserException
     * @throws UnauthorizedUserException
     */
    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer illegaltoken")
    @Test(expected = NotAuthorizedException.class)
    public void testStep007createBallotUnauthorizedUser(
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

        ballotService.create(ballot);

    }

    /**
     * Checks the create request contains an owner for the ballot.
     *
     * @param ballotService
     * @param userService
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     * @throws NoBallotOwnerException
     * @throws UnknownUserException
     * @throws UnauthorizedUserException
     */
    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @Test(expected = ClientErrorException.class)
    public void testStep008createBallotNoOwner(
            @ArquillianResteasyResource("rest") BallotService ballotService,
            @ArquillianResteasyResource("rest") UserService userService)
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

        ballotService.create(ballot);

    }

    /**
     * Checks the owner of the ballot is the same as the authoirzed user.
     *
     * @param ballotService
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     * @throws NoBallotOwnerException
     * @throws UnknownUserException
     * @throws UnauthorizedUserException
     */
    @RunAsClient
    @Header(name = "Authorization", value = "TestBearer cschaefer")
    @Test(expected = ClientErrorException.class)
    public void testStep009createBallotUnknownUser2(
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

        // let's try and create a ballot on behalf of another user
        ballot.setOwner(this.anotherUser);

        ballotService.create(ballot);

    }

    //    public void test09createBallotUnknownUser2(
    //            @ArquillianResteasyResource("rest/ballot") WebTarget webTarget)
    //                    throws JsonGenerationException,
    //                    JsonMappingException,
    //                    IOException,
    //                    NoBallotOwnerException,
    //                    UnknownUserException,
    //                    UnauthorizedUserException {
    //
    //        webTarget.
    //        Builder invocationBuilder = webTarget.request().header(name, value);
    //        Invocation invocation = invocationBuilder.buildPost(Entity.entity(new Ballot(), MediaType.APPLICATION_JSON_TYPE)).;
    //
    //        invocation.
    //
    //    }
}
