package ru.kata.spring.boot_security.rest.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.rest.exception_handling.IncorrectEmailException;
import ru.kata.spring.boot_security.rest.exception_handling.NoContentException;
import ru.kata.spring.boot_security.rest.exception_handling.NoSuchUserException;
import ru.kata.spring.boot_security.rest.model.Role;
import ru.kata.spring.boot_security.rest.model.User;
import ru.kata.spring.boot_security.rest.service.RoleService;
import ru.kata.spring.boot_security.rest.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> listRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    //  Получение всех пользователей из БД
    @GetMapping("/users")
    public ResponseEntity<List<User>> listUsers() {
        List<User> userList = userService.listUsers();
        if (userList == null || userList.isEmpty()) {
            throw new NoContentException("Список пользователей в Database пуст");      //  204 No Content: Если список пользователей пуст
        }
        return ResponseEntity.ok(userList);                                            //  200 OK: Если список пользователей успешно получен
    }

    //  Создание нового пользователя
    @PostMapping("/users")
    public ResponseEntity<?> addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrors(bindingResult));
        }
        if (userService.findByUseremail(user.getEmail()) != null) {
            throw new IncorrectEmailException("Email already exists!");
        }
        userService.add(user, user.getRoles());
        return ResponseEntity.ok(user);
    }

    //  Редактирование пользователя
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user, BindingResult bindingResult) {
        User existingUser = userService.findById(id);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrors(bindingResult));                                            // 400 Bad Request
        }
        if (existingUser == null) {
            throw new NoSuchUserException("Пользователь с ID: " + id + " не существует в Database!");                      // 404 Not Found
        }
        if (!existingUser.getEmail().equals(user.getEmail())) {
            if (userService.findByUseremail(user.getEmail()) != null) {
                throw new IncorrectEmailException("Email already exists!");
            }
        }
        user.setId(id);
        userService.update(user, user.getRoles());
        return ResponseEntity.ok(user);
    }

    //  Удаление пользователя
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new NoSuchUserException("User with ID: " + id + " not found in Database");
        }
        userService.delete(id);
        return ResponseEntity.ok("User with ID = " + id + " was successfully deleted from Database!");
//      return new ResponseEntity<String>("User deleted successfully!", HttpStatus.OK);                                   //  можно применить и такой подход
    }

//  Юрий, хотел уточнить, какой подход лучше использовать при возврате ResponseEntity при удалении пользователя?

    /*
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new NoSuchUserException("User not found!");
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();                   //  noContent() создаёт ResponseEntity с кодом состояния HTTP 204 No Content
    }
     */

    /*
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new NoSuchUserException("User not found!");
        }
        userService.delete(id);
        return ResponseEntity.ok().build();                            //  метод build() создает ResponseEntity без тела
    }
     */

    private Map<String, String> getErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }

    //  Получение пользователя по ID
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getOneUser(@PathVariable Long id) {
        User oneUser = userService.findById(id);
        if(oneUser == null) {
            throw new NoSuchUserException("Пользователь с ID: " + id + " не существует в Database!");        //  NoSuchUserException - 404 Not Found
        }
        return ResponseEntity.ok(oneUser);
    }
}

