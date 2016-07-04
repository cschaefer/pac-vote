package com.prodyna.pac.vote.service.repo.api;

import com.prodyna.pac.vote.service.api.model.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User create(User user);

    User findById(Integer userId);

    User findByName(String userName);

    User findByUid(String uid);

    User update(User user);

    void delete(Integer userId);

}
