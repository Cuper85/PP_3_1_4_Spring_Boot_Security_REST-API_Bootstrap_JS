package ru.kata.spring.boot_security.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.rest.model.User;
import ru.kata.spring.boot_security.rest.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> showUserPage(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByUseremail(email);
        return ResponseEntity.ok(user);
    }
}




