package com.prodyna.pac.vote.service.repo.impl;

import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.repo.api.UserRepository;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.slf4j.Logger;

@Stateless
public class UserRepositoryImpl implements UserRepository {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Override
    public User create(User user) {
        this.em.persist(user);
        return user;
    }

    @Override
    public User findById(Integer userId) {
        try {
            return this.em.find(User.class, userId);
        } catch (final NoResultException e) {
            this.log.info("No user found for userId [{}]", userId);
        }
        return null;
    }

    @Override
    public User findByName(String userName) {
        try {
            return (User) this.em.createNamedQuery("user.findByName").setParameter("userName", userName).getSingleResult();
        } catch (final NoResultException e) {
            this.log.info("No user found for userName [{}]", userName);
        }
        return null;
    }

    @Override
    public User findByUid(String uid) {
        try {
            return (User) this.em.createNamedQuery("user.findByUid").setParameter("uid", uid).getSingleResult();
        } catch (final NoResultException e) {
            this.log.info("No user found for uid [{}]", uid);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> findAll() {
        return this.em.createNamedQuery("user.all").getResultList();
    }

    @Override
    public User update(User user) {
        return this.em.merge(user);
    }

    @Override
    public void delete(Integer userId) {
        final User user = this.findById(userId);
        this.em.remove(user);
    }



}
