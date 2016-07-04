package com.prodyna.pac.vote.service.repo.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.producer.EntityManagerProducer;
import com.prodyna.pac.vote.service.producer.LoggerProducer;
import com.prodyna.pac.vote.service.repo.api.UserRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UserRepositoryTest {

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

    @PersistenceContext
    EntityManager em;

    @Inject
    UserTransaction utx;

    @Inject
    UserRepository userRepository;


    @After
    public void clearData() throws Exception {
        this.utx.begin();
        this.em.joinTransaction();
        System.out.println("Dumping old records...");
        this.em.createQuery("delete from User").executeUpdate();
        this.utx.commit();
    }


    @Test
    public void createUser() {

        final User user = new User();
        user.setUserName("user1");

        final User createdUser = this.userRepository.create(user);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getUserId());

    }

    @Test
    public void findUser() {

        final User user = new User();
        user.setUserName("user1");

        final User createdUser = this.userRepository.create(user);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getUserId());

        final User foundUser = this.userRepository.findById(createdUser.getUserId());

        assertEquals(createdUser, foundUser);
    }



    @Test
    public void deleteUser() {

        final User user = new User();
        user.setUserName("user1");

        final User createdUser = this.userRepository.create(user);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getUserId());

        User foundUser = this.userRepository.findById(createdUser.getUserId());

        this.userRepository.delete(foundUser.getUserId());

        foundUser = this.userRepository.findById(createdUser.getUserId());
        assertNull(foundUser);

    }

}
