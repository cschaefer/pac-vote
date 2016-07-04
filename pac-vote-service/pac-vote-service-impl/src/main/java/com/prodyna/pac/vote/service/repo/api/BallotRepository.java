package com.prodyna.pac.vote.service.repo.api;

import com.prodyna.pac.vote.service.api.model.Ballot;

import java.util.List;

/**
 * Ballot Repository for {@link Ballot} domain objects.
 * 
 * @author cschaefer
 *
 */
public interface BallotRepository {

    /**
     * Find all current {@link Ballot}
     * @return all ballots
     */
    List<Ballot> findAll();

    /**
     * Create a ballot.
     * @param ballot to create
     * @return new ballot
     */
    Ballot create(Ballot ballot);

    /**
     * Find a ballot by id.
     * @param ballotId for the {@link Ballot}
     * @return existing ballot
     */
    Ballot findById(Integer ballotId);

    /**
     * Update a ballot given an id.
     * @param ballotId for an existing {@link Ballot}
     * @param ballot to be updated
     * @return updated ballot
     */
    Ballot update(Integer ballotId, Ballot ballot);

    /**
     * Delete an existing ballot.
     * @param ballotId for an existing {@link Ballot}
     */
    void delete(Integer ballotId);
    
}
