package com.app.foodbackend.security.user.service;

import com.app.foodbackend.security.user.entity.Authority;
import com.app.foodbackend.security.user.entity.Role;
import com.app.foodbackend.security.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final AuthorityService authorityService;

    public List<Role> initializeDefaultRoles() {
        List<Authority> authorities =  authorityService.initializeDefaultAuthorities();

        if(roleRepository.existsByName("ADMIN") == false){
            Role admin = new Role();
            admin.setName("ADMIN");
            admin.setAuthorities(authorities.stream().filter(authority -> authority.getName().startsWith("admin:")).collect(Collectors.toList()));
            roleRepository.save(admin);
        }
        if(roleRepository.existsByName("VISITOR") == false){
            Role operator = new Role();
            operator.setName("VISITOR");
            operator.setAuthorities(authorities.stream().filter(authority -> authority.getName().startsWith("visitor:")).collect(Collectors.toList()));
            roleRepository.save(operator);
        }

        return roleRepository.findAll();
    }

    public Role getRole(String role) {
        return roleRepository.findByName(role);
    }
}
