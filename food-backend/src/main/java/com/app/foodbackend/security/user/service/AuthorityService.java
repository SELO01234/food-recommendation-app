package com.app.foodbackend.security.user.service;

import com.app.foodbackend.security.user.entity.Authority;
import com.app.foodbackend.security.user.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public List<Authority> initializeDefaultAuthorities() {

        List<String> authorities = new ArrayList<>();
        authorities.add("admin:read");
        authorities.add("admin:write");
        authorities.add("admin:delete");
        authorities.add("admin:update");

        authorities.add("visitor:read");
        authorities.add("visitor:write");
        authorities.add("visitor:delete");
        authorities.add("visitor:update");

        for(int i=0; i< authorities.size(); ++i){
            if(authorityRepository.existsByName(authorities.get(i)) == false){
                authorityRepository.save(Authority.builder().name(authorities.get(i)).build());
            }
        }

        return authorityRepository.findAll();
    }
}
