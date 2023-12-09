package com.app.foodbackend.security.user.service;

import com.app.foodbackend.security.user.entity.Role;
import com.app.foodbackend.security.user.entity.User;
import com.app.foodbackend.security.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @PostConstruct
    private void initializeDefaultUsers(){
        List<Role> roles = roleService.initializeDefaultRoles();

        List<User> users = new ArrayList<>();

        User adminUser = new User();
        User visitorUser = new User();

        for(int i=0; i < roles.size(); ++i){
            if(roles.get(i).getName().equals("ADMIN")){
                adminUser.setRole(roles.get(i));
            }
            if(roles.get(i).getName().equals("VISITOR")){
                visitorUser.setRole(roles.get(i));
            }
        }

        if(userRepository.existsByEmail("furkan@gmail.com") == false){
            adminUser.setFirstName("Furkan");
            adminUser.setLastName("Furkan");
            adminUser.setEmail("furkan@gmail.com");
            adminUser.setUserName("furkan");
            adminUser.setPassword(passwordEncoder.encode("123"));
            users.add(adminUser);
        }
        if(userRepository.existsByEmail("ali@gmail.com") == false){
            visitorUser.setFirstName("Ali");
            visitorUser.setLastName("Ali");
            visitorUser.setEmail("ali@gmail.com");
            visitorUser.setUserName("ali");
            visitorUser.setPassword(passwordEncoder.encode("123"));
            users.add(visitorUser);
        }

        userRepository.saveAll(users);
    }
}
