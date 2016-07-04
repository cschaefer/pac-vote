package com.prodyna.pac.vote.service.repo.api;

import com.prodyna.pac.vote.service.api.model.Choice;

import java.util.List;

public interface ChoiceRepository {

    List<Choice> findAll();

    Choice create(Choice choice);

    Choice findById(Integer choiceId);

    Choice update(Integer choiceId, Choice choice);

    void delete(Integer choiceId);
    
}
