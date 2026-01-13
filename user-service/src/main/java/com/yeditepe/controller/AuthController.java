package com.yeditepe.controller;

import com.yeditepe.config.JwtUtils;
import com.yeditepe.dto.JwtResponse;
import com.yeditepe.dto.LoginRequest;
import com.yeditepe.dto.RegisterRequest;
import com.yeditepe.entity.User;
import com.yeditepe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
        private final UserService userService;
        private final JwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;

        @PostMapping("/register")
        public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
                User savedUser = userService.registerUser(registerRequest);
                return ResponseEntity.ok("Kullanıcı başarıyla kaydedildi: " + savedUser.getUsername());
        }

        @PostMapping("/login")
        public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
                // 1. Kullanıcı adı ve şifreyi kontrol etmek
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                loginRequest.getPassword()));

                // 2. Güvenlik bağlamını güncellemek
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 3. Kullanıcıya özel JWT token üretmek (Rolleri de içine ekliyoruz)
                String jwt = jwtUtils.generateJwtToken(authentication.getName(), authentication.getAuthorities());

                // 4. Kullanıcı rollerini almak
                List<String> roles = authentication.getAuthorities().stream()
                                .map(item -> item.getAuthority())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(new JwtResponse(jwt, authentication.getName(), roles));
        }
}
