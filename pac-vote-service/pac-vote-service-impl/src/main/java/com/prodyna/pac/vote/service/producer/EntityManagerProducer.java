package com.prodyna.pac.vote.service.producer;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerProducer {


    @Produces
    @Dependent
    @PersistenceContext
    private EntityManager entityManager;
    
}
