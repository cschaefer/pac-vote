package com.prodyna.pac.vote.service.repo.impl;

import com.prodyna.pac.vote.service.api.model.ChoiceCount;
import com.prodyna.pac.vote.service.api.model.Vote;
import com.prodyna.pac.vote.service.repo.api.VoteRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;

@Stateless
public class VoteRepositoryImpl implements VoteRepository {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Override
    public Vote create(Vote vote) {
        vote.setVoteDate(LocalDate.now());
        this.em.persist(vote);
        this.em.flush();
        return vote;
    }

    @Override
    public Vote findById(Integer voteId) {
        return this.em.find(Vote.class, voteId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Vote> findAll() {
        return this.em.createNamedQuery("vote.all").getResultList();
    }

    @Override
    public Boolean hasUserVoted(Integer userId, Integer ballotId) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("hasUserVoted userId=[{}], ballotId=[{}]", userId, ballotId);
        }
        final Query query = this.em.createNamedQuery("vote.userVoted");
        query.setParameter("userId", userId);
        query.setParameter("ballotId", ballotId);

        try {
            final Object result = query.getSingleResult();
            if (Objects.isNull(result)) {
                return false;
            }

        } catch (final NoResultException e) {
            return false;
        }

        return true;
    }

    @Override
    public List<Vote> findBallotVotes(Integer ballotId) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("findBallotVotes ballotId=[{}]", ballotId);
        }
        final Query query = this.em.createNamedQuery("vote.ballotVotes");
        query.setParameter("ballotId", ballotId);

        @SuppressWarnings("unchecked")
        final
        List<Vote> votes = query.getResultList();
        return votes;
    }

    @Override
    public List<Vote> findMyBallotVotes(Integer userId) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("findMyBallotVotes userId=[{}]", userId);
        }

        final Query query = this.em.createNamedQuery("vote.myBallotVotes");
        query.setFlushMode(FlushModeType.AUTO);
        query.setParameter("userId", userId);

        @SuppressWarnings("unchecked")
        final
        List<Vote> votes = query.getResultList();
        return votes;
    }


    @Override
    public List<ChoiceCount> findBallotResult(Integer ballotId) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("findBallotResult ballotId=[{}]", ballotId);
        }
        final Query query = this.em.createNamedQuery("vote.ballotResult");
        query.setParameter("ballotId", ballotId);

        @SuppressWarnings("unchecked")
        final
        List<ChoiceCount> choiceCounts = query.getResultList();
        return choiceCounts;

    }


    @Override
    public void delete(Integer voteId) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("delete ballotId=[{}]", voteId);
        }
        final Vote vote = this.findById(voteId);
        this.em.remove(vote);
    }


}
