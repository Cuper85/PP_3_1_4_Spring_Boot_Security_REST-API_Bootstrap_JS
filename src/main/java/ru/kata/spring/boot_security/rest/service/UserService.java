package ru.kata.spring.boot_security.rest.service;

import ru.kata.spring.boot_security.rest.model.Role;
import ru.kata.spring.boot_security.rest.model.User;

import java.util.List;

public interface UserService {

    List<User> listUsers();

    void add(User user, List<Role> roles);

    void update(User user, List<Role> roles);

    void delete(Long id);

    User findById(Long id);

    User findByUseremail(String email);
}
