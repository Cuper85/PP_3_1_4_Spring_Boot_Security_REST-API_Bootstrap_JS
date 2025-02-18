package ru.kata.spring.boot_security.rest.service;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.rest.model.Role;
import ru.kata.spring.boot_security.rest.model.User;
import ru.kata.spring.boot_security.rest.repository.RoleRepository;
import ru.kata.spring.boot_security.rest.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> listUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Role> roles = user.getRoles();
            {                                                                     // Ленивая загрузка ролей
                for (Role role : roles) {
                    Hibernate.initialize(role);
                }
            }
        }
        return users;
    }

    @Override
    @Transactional
    public void add(User user, List<Role> roles) {
        if (userRepository.findByUseremail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists!");
        }
        if (roles == null || roles.isEmpty()) {                                    // Если роли не переданы, присваиваем роль по умолчанию (в данном случае - USER)
            Role defaultRole = roleRepository.findById(2L).orElse(null);     // ID роли "USER" в БД - 2
            if (defaultRole != null) {
                roles = List.of(defaultRole);
            }
        }
        List<Role> managedRoles = roles.stream()                                    // Загружаем роли из базы данных, чтобы они были управляемыми
            .map(role -> roleRepository.findById(role.getId()).orElseThrow(() -> new EntityNotFoundException("Role not found"))).collect(Collectors.toList());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(managedRoles);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(User updatedUser, List<Role> roles) {
        if (updatedUser == null || updatedUser.getId() == null) {
            throw new IllegalArgumentException("User ID не может быть null");
        }
        User existingUser = findById(updatedUser.getId());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setEmail(updatedUser.getEmail());
        // Обновление ролей
        if (roles != null && !roles.isEmpty()) {
            // Получаем managed роли из базы данных
            List<Role> managedRoles = roles.stream()
                    .map(role -> roleRepository.findById(role.getId()).orElseThrow(() -> new EntityNotFoundException("Role not found"))).collect(Collectors.toList());
            existingUser.setRoles(managedRoles);
        }
        else {
            existingUser.setRoles(existingUser.getRoles());
        }
        if (!updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
        } else {
            existingUser.setPassword(existingUser.getPassword());
        }
    userRepository.save(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with ID: " + id + " not found in Database"));
        user.getRoles().size();
        return user;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with ID: " + id + " not found in Database"));
        user.getRoles().clear();
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUseremail(String email) {
        return userRepository.findByUseremail(email);
    }
}
