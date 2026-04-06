package com.backend.forensic_backend.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.backend.forensic_backend.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtFilter;

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> {})   // 👈 ADD THIS LINE
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
}