package com.backend.forensic_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.forensic_backend.model.User;
import com.backend.forensic_backend.repository.UserRepository;
import com.backend.forensic_backend.util.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtUtil jwt;

    public String login(String username, String password) {
        User user = repo.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            return jwt.generateToken(username);
        }
        return null;
    }

        @Autowired
    private UserRepository userRepo;

    public boolean signup(String username, String password) {

        // check if user exists
        if (userRepo.findByUsername(username) != null) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        userRepo.save(user);

        return true;
}
}