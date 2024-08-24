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
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;
    private final UserService userService;

    public PermissionController(PermissionService permissionService, UserService userService) {
        this.permissionService = permissionService;
        this.userService = userService;
    }

    @PostMapping("/permissions")
    @ApiMessage("create a permission")
    public ResponseEntity<Permission> createNewPermission(@Valid @RequestBody Permission reqPermission)
            throws IdInvalidException {
        // check exist
        if (this.permissionService.isPermissionExist(reqPermission)) {
            throw new IdInvalidException("Permission existed");
        }
        // create new
        Permission permission = this.permissionService.handleCreatePermission(reqPermission);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    @PutMapping("/permissions")
    @ApiMessage("update a permission")
    public ResponseEntity<Permission> updateAPermission(@RequestBody Permission reqPermission)
            throws IdInvalidException {

        // check exist by id
        Permission checkPermission = this.permissionService.fetchPermissionById(reqPermission.getId());
        if (checkPermission == null) {
            throw new IdInvalidException("Permission not found");
        }

        boolean checkExist = this.permissionService.isPermissionExist(reqPermission);
        if (checkExist) {
            // check name
            if (this.permissionService.isSameName(reqPermission))
                throw new IdInvalidException("Permission info existed");
        }
        // update
        Permission currentPermission = this.permissionService.handleUpdatePermission(reqPermission);
        // convert user to ResUpdateUserDTO to display
        return ResponseEntity.ok(currentPermission);
    }

    @GetMapping("/permissions")
    @ApiMessage("fetch all permissions")
    public ResponseEntity<ResultPaginationDTO> fetchAllPermissions(
            @Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.fetchAllPermissions(spec, pageable));
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("fetch permission by id")
    public ResponseEntity<Permission> fetchPermissionById(@PathVariable("id") long id) throws IdInvalidException {
        Permission permission = this.permissionService.fetchPermissionById(id);
        if (permission == null) {
            throw new IdInvalidException("Permission not found");
        }
        // return ResponseEntity.status(HttpStatus.OK).body(user);
        return ResponseEntity.ok(permission);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete permission by id")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        Permission permission = this.permissionService.fetchPermissionById(id);
        if (permission == null) {
            throw new IdInvalidException("Permission not found");
        }
        this.permissionService.handleDeletePermission(id);
        // return ResponseEntity.status(HttpStatus.OK).body("id: " + id);
        return ResponseEntity.ok(null);
    }

}
