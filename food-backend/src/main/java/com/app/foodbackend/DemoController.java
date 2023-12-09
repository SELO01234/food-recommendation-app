package com.app.foodbackend;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo-controller")
public class DemoController {

    @GetMapping("/hello")
    @PreAuthorize("hasAuthority('visitor:write')" + "&&" + "hasRole('VISITOR')")
    public String hello(){
        return "Hello";
    }
}
