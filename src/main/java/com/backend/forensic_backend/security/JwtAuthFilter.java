package com.backend.forensic_backend.security;

import com.backend.forensic_backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
protected void doFilterInternal(HttpServletRequest request,
                               HttpServletResponse response,
                               FilterChain filterChain)
        throws ServletException, IOException {

    String path = request.getServletPath();

    // ✅ ALLOW LOGIN & SIGNUP WITHOUT TOKEN
    if (path.startsWith("/api/auth")) {
        filterChain.doFilter(request, response);
        return;
    }

    String authHeader = request.getHeader("Authorization");

    // ❌ No token → block request
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Missing or invalid Authorization header");
        return;
    }

    String token = authHeader.substring(7);

    try {
        String username = jwtUtil.extractUsername(token);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("USER"))
                );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        System.out.println("✅ Authenticated user: " + username);

    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid JWT token");
        return;
    }

    filterChain.doFilter(request, response);
}
}