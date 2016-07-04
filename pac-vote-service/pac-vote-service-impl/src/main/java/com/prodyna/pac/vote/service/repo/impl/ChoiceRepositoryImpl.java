package com.prodyna.pac.vote.service.repo.impl;

import com.prodyna.pac.vote.service.api.model.Choice;
import com.prodyna.pac.vote.service.repo.api.ChoiceRepository;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

@Stateless
public class ChoiceRepositoryImpl implements ChoiceRepository {

    @Inject
    private Logger log;
    
    @Inject
    private EntityManager em;
    
    @Override
    public Choice create(Choice choice) {
        em.persist(choice);
        return choice;
    }
    
    @Override
    public Choice findById(Integer choiceId) {
        return em.find(Choice.class, choiceId);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Choice> findAll() {
        return em.createNamedQuery("choice.all").getResultList();
    }
    
    @Override
    public Choice update(Integer choiceId, Choice choice) {
        log.info("Updating choiceId {}", choiceId);
        choice.setChoiceId(choiceId);
        return em.merge(choice);
    }
    
    @Override
    public void delete(Integer choiceId) {
        Choice choice = findById(choiceId);
        em.remove(choice);
    }

    
}
