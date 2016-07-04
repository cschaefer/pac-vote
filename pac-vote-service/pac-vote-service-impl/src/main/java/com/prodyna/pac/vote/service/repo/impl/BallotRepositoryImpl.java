package com.prodyna.pac.vote.service.repo.impl;

import com.prodyna.pac.vote.service.api.model.Ballot;
import com.prodyna.pac.vote.service.repo.api.BallotRepository;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

@Stateless
public class BallotRepositoryImpl implements BallotRepository {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Override
    public Ballot create(Ballot ballot) {
        this.em.persist(ballot);
        return ballot;
    }

    @Override
    public Ballot findById(Integer ballotId) {
        return this.em.find(Ballot.class, ballotId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Ballot> findAll() {
        return this.em.createNamedQuery("ballot.all").getResultList();
    }

    @Override
    public Ballot update(Integer ballotId, Ballot ballot) {
        this.log.info("Updating ballot {}", ballotId);
        ballot.setBallotId(ballotId);
        return this.em.merge(ballot);
    }

    @Override
    public void delete(Integer ballotId) {
        final Ballot ballot = this.findById(ballotId);
        this.em.remove(ballot);
    }


}
