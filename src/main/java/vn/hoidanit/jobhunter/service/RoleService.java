package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public RoleService(RoleRepository roleRepository, PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
    }

    public Role fetchRoleById(long id) {
        Optional<Role> rOptional = this.roleRepository.findById(id);
        if (rOptional.isPresent())
            return rOptional.get();
        return null;
    }

    public List<Role> fetchListRolesByListIds(List<Long> listIds) {
        return this.roleRepository.findByIdIn(listIds);
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public boolean handleCheckUpdate(Role reqRole) {
        String oldName = this.fetchRoleById(reqRole.getId()).getName();
        boolean check = reqRole.getName().equals(oldName);
        if (this.existByName(reqRole.getName()) && !check)
            return false;
        return true;
    }

    public Role handleCreateRole(Role reqRole) {
        // special
        if (reqRole.getPermissions() != null) {
            List<Long> idPermissions = reqRole.getPermissions()
                    .stream().map(i -> i.getId()).collect(Collectors.toList());
            reqRole.setPermissions(this.permissionService.fetchPermissionsByIds(idPermissions));
        }
        return this.roleRepository.save(reqRole);
    }

    public Role handleUpdateRole(Role reqRole) {
        Role currentRole = this.fetchRoleById(reqRole.getId());
        currentRole.setActive(reqRole.isActive());
        if (reqRole.getName() != null && this.existByName(reqRole.getName())) {
            currentRole.setName(reqRole.getName());
        }
        if (reqRole.getDescription() != null) {
            currentRole.setDescription(reqRole.getDescription());
        }
        if (reqRole.getPermissions() != null) {
            List<Long> idPermissions = reqRole.getPermissions()
                    .stream().map(i -> i.getId()).collect(Collectors.toList());
            currentRole.setPermissions(this.permissionService.fetchPermissionsByIds(idPermissions));
        }
        return this.roleRepository.save(currentRole);
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRoles = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageRoles.getTotalPages());
        mt.setTotal(pageRoles.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageRoles.getContent());
        return rs;
    }

    public void handleDeleteARole(long id) {
        this.roleRepository.deleteById(id);
    }
}
