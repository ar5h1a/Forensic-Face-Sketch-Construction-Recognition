package com.backend.forensic_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.forensic_backend.model.User;
import com.backend.forensic_backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        String token = service.login(user.getUsername(), user.getPassword());

        if (token != null) return token;
        return "Invalid credentials";
    }
}