package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public User createNewUser(@RequestBody User user) {
        User ericUser = this.userService.handleCreateAUser(user);
        return ericUser;
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        this.userService.handleDeleteUser(id);
        return "id: " + id;
    }

    @GetMapping("/user/{id}")
    public User fetchUserById(@PathVariable("id") long id) {
        return this.userService.fetchUserById(id);
    }

    @GetMapping("/user")
    public List<User> fetchAllUsers() {
        List<User> users = this.userService.fetchAllUsers();
        return users;
    }

    @PutMapping("/user")
    public User updateAUser(@RequestBody User user) {
        User ericUser = this.userService.handleUpdateUser(user);
        return ericUser;
    }

}
