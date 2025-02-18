package ru.kata.spring.boot_security.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kata.spring.boot_security.rest.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}


