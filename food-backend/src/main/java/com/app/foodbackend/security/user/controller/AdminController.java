package com.app.foodbackend.security.user.controller;

import com.app.foodbackend.security.auth.dto.RegisterRequest;
import com.app.foodbackend.security.user.requestDTO.UserRequest;
import com.app.foodbackend.security.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @PostMapping("/add-user")
    @PreAuthorize("hasAuthority('admin:write')")
    public ResponseEntity<String> saveUser(@RequestBody RegisterRequest user){
        try{
            userService.saveUser(user);
            return ResponseEntity.ok().body("User added successfully");
        }
        catch(Exception exception){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete-user/{id}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Integer id){
        try{
            userService.deleteUser(id);
            return ResponseEntity.ok().body("User deleted successfully");
        }
        catch(Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/update-user")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<String> updateUser(@RequestBody UserRequest user){
        try{
            userService.updateUser(user);
            return ResponseEntity.ok().body("User updated successfully");
        }
        catch(Exception exception){
            return ResponseEntity.notFound().build();
        }
    }
}
