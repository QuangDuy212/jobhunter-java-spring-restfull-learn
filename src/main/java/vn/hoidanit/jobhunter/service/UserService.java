package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
            CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateAUser(User user) {
        // check company
        if (user.getCompany() != null) {
            Optional<Company> company = this.companyService.fetchCompanyById(user.getCompany().getId());
            user.setCompany(company.isPresent() ? company.get() : null);
        }

        // check role
        if (user.getRole() != null) {
            Role role = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(role != null ? role : null);
        }
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public boolean isIdExist(long id) {
        return this.userRepository.existsById(id);
    }

    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        List<ResUserDTO> listUser = new ArrayList<ResUserDTO>();
        for (User item : pageUser.getContent()) {
            listUser.add(this.convertUserToResUserDTO(item));
        }
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(listUser);
        return rs;
    }

    public User fetchUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent())
            return user.get();
        return null;
    }

    public User handleUpdateUser(User user) {
        User currentUser = this.fetchUserById(user.getId());
        if (currentUser != null) {
            currentUser.setName(user.getName());
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());
            currentUser.setGender(user.getGender());

            // check company
            if (user.getCompany() != null) {
                Optional<Company> company = this.companyService.fetchCompanyById(user.getCompany().getId());
                currentUser.setCompany(company.isPresent() ? company.get() : null);
            }

            // check role
            if (user.getRole() != null) {
                Role role = this.roleService.fetchRoleById(user.getRole().getId());
                user.setRole(role != null ? role : null);
            }
            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        if (user.getCompany() != null) {
            ResCreateUserDTO.CompanyUser infoCompany = new ResCreateUserDTO.CompanyUser();
            infoCompany.setId(user.getCompany().getId());
            infoCompany.setName(user.getCompany().getName());
            res.setCompany(infoCompany);
        }
        return res;
    }

    public ResUserDTO convertUserToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        res.setUdpatedAt(user.getUpdatedAt());
        if (user.getCompany() != null) {
            ResUserDTO.CompanyUser companyInfo = new ResUserDTO.CompanyUser();
            companyInfo.setId(user.getCompany().getId());
            companyInfo.setName(user.getCompany().getName());
            res.setCompany(companyInfo);
        }
        if (user.getRole() != null) {
            ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }
        return res;
    }

    public ResUpdateUserDTO convertUserToUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setName(user.getName());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setAge(user.getAge());
        res.setUdpatedAt(user.getUpdatedAt());
        res.setId(user.getId());
        if (user.getCompany() != null) {
            ResUpdateUserDTO.CompanyUser company = new ResUpdateUserDTO.CompanyUser();
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }
        if (user.getRole() != null) {
            ResUpdateUserDTO.RoleUser roleUser = new ResUpdateUserDTO.RoleUser();
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }

        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public void handleLogout(User user) {
        user.setRefreshToken(null);
        this.userRepository.save(user);
    }
}
