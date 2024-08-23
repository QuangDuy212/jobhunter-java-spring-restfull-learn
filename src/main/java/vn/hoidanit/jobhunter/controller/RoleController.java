package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("create a role")
    public ResponseEntity<Role> createNewRole(@Valid @RequestBody Role reqRole)
            throws IdInvalidException {
        if (this.roleService.existByName(reqRole.getName())) {
            throw new IdInvalidException("Role name existed");
        }
        Role role = this.roleService.handleCreateRole(reqRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PutMapping("/roles")
    @ApiMessage("update a role")
    public ResponseEntity<Role> updateARole(@RequestBody Role reqRole)
            throws IdInvalidException {
        Role checkRole = this.roleService.fetchRoleById(reqRole.getId());
        if (checkRole == null) {
            throw new IdInvalidException("Role not found");
        }
        boolean checkExist = this.roleService.handleCheckUpdate(reqRole);
        if (!checkExist) {
            throw new IdInvalidException("Role name existed");
        }
        Role currentRole = this.roleService.handleUpdateRole(reqRole);
        // convert user to ResUpdateUserDTO to display
        return ResponseEntity.ok(currentRole);
    }

    @GetMapping("/roles")
    @ApiMessage("fetch all roles")
    public ResponseEntity<ResultPaginationDTO> fetchAllRoles(
            @Filter Specification<Role> spec,
            Pageable pageable) {

        // fetch all
        // return ResponseEntity.status(HttpStatus.OK).body(users);
        return ResponseEntity.ok(this.roleService.fetchAllRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("fetch role by id")
    public ResponseEntity<Role> fetchRoleById(@PathVariable("id") long id) throws IdInvalidException {
        Role role = this.roleService.fetchRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role not found");
        }
        // return ResponseEntity.status(HttpStatus.OK).body(user);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("delete role by id")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        Role role = this.roleService.fetchRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role not found");
        }
        this.roleService.handleDeleteARole(id);
        // return ResponseEntity.status(HttpStatus.OK).body("id: " + id);
        return ResponseEntity.ok(null);
    }

}
