package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.service.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@RequestBody User user) {
        String hashPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPass);
        User ericUser = this.userService.handleCreateAUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ericUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        if (id >= 1500) {
            throw new IdInvalidException("Id khong lon hon 1500");
        }
        this.userService.handleDeleteUser(id);
        // return ResponseEntity.status(HttpStatus.OK).body("id: " + id);
        return ResponseEntity.ok("id: " + id);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> fetchUserById(@PathVariable("id") long id) {
        User user = this.userService.fetchUserById(id);
        // return ResponseEntity.status(HttpStatus.OK).body(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> fetchAllUsers() {
        List<User> users = this.userService.fetchAllUsers();
        // return ResponseEntity.status(HttpStatus.OK).body(users);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateAUser(@RequestBody User user) {
        User ericUser = this.userService.handleUpdateUser(user);
        // return ResponseEntity.status(HttpStatus.OK).body(ericUser);
        return ResponseEntity.ok(ericUser);
    }

}
