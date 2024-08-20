package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.user.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.user.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.user.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("create a user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isExists = this.userService.isEmailExist(user.getEmail());
        if (isExists) {
            throw new IdInvalidException("Email đã tồn tại, vui lòng nhập lại!");
        }
        String hashPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPass);
        User ericUser = this.userService.handleCreateAUser(user);
        // convert to ResCreateUserDTO to display
        ResCreateUserDTO res = this.userService.convertToResCreateUserDTO(ericUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("delete user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        // return ResponseEntity.status(HttpStatus.OK).body("id: " + id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> fetchUserById(@PathVariable("id") long id) throws IdInvalidException {
        boolean checkExist = this.userService.isIdExist(id);
        if (!checkExist) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        User user = this.userService.fetchUserById(id);
        // convert user to ResUserDTO to display
        ResUserDTO res = this.userService.convertUserToResUserDTO(user);
        // return ResponseEntity.status(HttpStatus.OK).body(user);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUsers(
            @Filter Specification<User> spec,
            Pageable pageable) {

        // fetch all
        // return ResponseEntity.status(HttpStatus.OK).body(users);
        return ResponseEntity.ok(this.userService.fetchAllUsers(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("update a user")
    public ResponseEntity<ResUpdateUserDTO> updateAUser(@RequestBody User user) throws IdInvalidException {
        boolean checkExist = this.userService.isIdExist(user.getId());
        if (!checkExist) {
            throw new IdInvalidException("User với id = " + user.getId() + " không tồn tại");
        }
        User ericUser = this.userService.handleUpdateUser(user);
        // convert user to ResUpdateUserDTO to display
        ResUpdateUserDTO res = this.userService.convertUserToUpdateUserDTO(ericUser);
        // return ResponseEntity.status(HttpStatus.OK).body(ericUser);
        return ResponseEntity.ok(res);
    }

}
