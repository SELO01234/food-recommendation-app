package com.app.foodbackend.security.user.controller;

import com.app.foodbackend.security.user.requestDTO.UserRequest;
import com.app.foodbackend.security.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')" + "||" + "hasRole('VISITOR')")
public class UserController {

    private final UserService userService;

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('admin:delete')" + "||" + "hasAuthority('visitor:delete')")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Integer id){
        try{
            userService.controlRole(id);
            userService.deleteUser(id);
            return ResponseEntity.ok().body("User deleted successfully");
        }
        catch(Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/update")
    @PreAuthorize("hasAuthority('admin:update')" + "||" + "hasAuthority('visitor:update')")
    public ResponseEntity<String> updateUser(@RequestBody UserRequest user){
        try{
            userService.controlRole(user.getId());
            userService.updateUser(user);
            return ResponseEntity.ok().body("User updated successfully");
        }
        catch(Exception exception){
            return ResponseEntity.notFound().build();
        }
    }
}
