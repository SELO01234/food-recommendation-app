package com.app.foodbackend.security.auth.controller;

import com.app.foodbackend.security.auth.dto.AuthenticationRequest;
import com.app.foodbackend.security.auth.dto.AuthenticationResponse;
import com.app.foodbackend.security.auth.dto.RegisterRequest;
import com.app.foodbackend.security.auth.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
        try {
            authenticationService.register(request);
            return ResponseEntity.ok().body("Registration is successful");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok().body(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request, HttpServletResponse response){
        try {
            return ResponseEntity.ok().body(authenticationService.refreshToken(request, response));
        }
        catch (IOException exception){
            return ResponseEntity.badRequest().build();
        }
    }
}
