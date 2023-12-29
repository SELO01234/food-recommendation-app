package com.app.foodbackend.security.auth.service;

import com.app.foodbackend.security.auth.dto.AuthenticationRequest;
import com.app.foodbackend.security.auth.dto.AuthenticationResponse;
import com.app.foodbackend.security.auth.dto.RegisterRequest;
import com.app.foodbackend.security.config.JwtService;
import com.app.foodbackend.security.user.entity.User;
import com.app.foodbackend.security.user.repository.UserRepository;
import com.app.foodbackend.security.user.service.RoleService;
import com.app.foodbackend.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request) throws Exception{

        if(
                request == null ||
                userRepository.existsByUserName(request.getUserName()) || userRepository.existsByEmail(request.getEmail()) ||
                !request.getEmail().matches(UserUtil.EMAIL_REGEX)
        ){
            throw new Exception();
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleService.getRole("VISITOR"))
                .build();

        userRepository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user;

        if(request.getUsername().matches(UserUtil.EMAIL_REGEX)){
            user = userRepository.findByEmail(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        else{
            user = userRepository.findByUserName(request.getUsername()).orElseThrow();
        }

        var access_token = jwtService.generateToken(user, "access_token");
        var refresh_token = jwtService.generateToken(user, "refresh_token");

        return AuthenticationResponse.builder()
                .access_token(access_token)
                .refresh_token(refresh_token)
                .build();
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        final String refreshToken;
        final String userName;
        if(authorizationHeader == null && !authorizationHeader.startsWith("Bearer ")){
            return;
        }

        refreshToken = authorizationHeader.substring(7);
        userName= jwtService.extractUsername(refreshToken) ;

        if(userName != null){
            var userDetails = this.userRepository.findByUserName(userName).orElseThrow();
            if(jwtService.isTokenValid(refreshToken, userDetails)){
                var accessToken = jwtService.generateToken(userDetails, "access_token");
                var authResponse = AuthenticationResponse.builder()
                        .access_token(accessToken)
                        .refresh_token(refreshToken)
                        .build();

                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
