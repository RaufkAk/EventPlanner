package com.yeditepe.eventservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public AuthTokenFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            if (header != null) {
                System.out.println("Auth Filter: Authorization header found");
            }

            String token = null;
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            }

            if (token != null) {
                boolean valid = jwtUtils.validateJwtToken(token);
                System.out.println("Auth Filter: Token valid: " + valid);

                if (valid) {
                    String username = jwtUtils.getUserNameFromJwtToken(token);
                    List<String> roles = jwtUtils.getRolesFromJwtToken(token);
                    System.out.println("Auth Filter: User: " + username + ", Roles: " + roles);

                    if (roles != null) {
                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(r -> {
                                    // Remove any existing ROLE_ prefix before adding it correctly
                                    String cleanRole = r.replace("ROLE_", "");
                                    return new SimpleGrantedAuthority("ROLE_" + cleanRole);
                                })
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                username, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("Auth Filter: SecurityContext updated with authorities: " + authorities);
                    } else {
                        System.out.println("Auth Filter: No roles found in token");
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Auth Filter Error: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
