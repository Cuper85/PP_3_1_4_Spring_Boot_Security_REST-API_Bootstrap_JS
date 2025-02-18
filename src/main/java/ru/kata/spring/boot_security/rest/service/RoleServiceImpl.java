package ru.kata.spring.boot_security.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.rest.model.Role;
import ru.kata.spring.boot_security.rest.repository.RoleRepository;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getRolesByIds(List<Long> ids) {
        return roleRepository.findAllById(ids);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
