package ru.kata.spring.boot_security.rest.service;

import ru.kata.spring.boot_security.rest.model.Role;

import java.util.List;

public interface RoleService {

    List<Role> getRolesByIds(List<Long> ids);

    List<Role> getAllRoles();
}
