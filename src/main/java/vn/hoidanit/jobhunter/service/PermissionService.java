package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isSameName(Permission p) {
        Permission pDB = this.fetchPermissionById(p.getId());
        if (pDB == null)
            return false;
        return pDB.getName().equals(p.getName());
    }

    public boolean isPermissionExist(Permission p) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(),
                p.getApiPath(),
                p.getMethod());
    }

    public Permission fetchPermissionById(long id) {
        Optional<Permission> pOptional = this.permissionRepository.findById(id);
        if (pOptional.isPresent())
            return pOptional.get();
        return null;
    }

    public List<Permission> fetchPermissionsByIds(List<Long> listIds) {
        return this.permissionRepository.findByIdIn(listIds);
    }

    public Permission handleCreatePermission(Permission reqPermission) {
        // special
        return this.permissionRepository.save(reqPermission);
    }

    public Permission handleUpdatePermission(Permission reqPermission) {
        Permission currentPermission = this.fetchPermissionById(reqPermission.getId());
        if (currentPermission != null) {
            if (reqPermission.getName() != null)
                currentPermission.setName(reqPermission.getName());
            if (reqPermission.getApiPath() != null)
                currentPermission.setApiPath(reqPermission.getApiPath());
            if (reqPermission.getMethod() != null)
                currentPermission.setMethod(reqPermission.getMethod());
            if (reqPermission.getModule() != null)
                currentPermission.setModule(reqPermission.getModule());
        }
        return this.permissionRepository.save(currentPermission);
    }

    public ResultPaginationDTO fetchAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pagePermission.getTotalPages());
        mt.setTotal(pagePermission.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pagePermission.getContent());
        return rs;
    }

    public void handleDeletePermission(long id) {
        // delete permission_role
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        Permission currentPermission = permissionOptional.get();
        currentPermission.getRoles().forEach(i -> i.getPermissions().remove(currentPermission));

        // delete permission
        this.permissionRepository.delete(currentPermission);
    }

}
