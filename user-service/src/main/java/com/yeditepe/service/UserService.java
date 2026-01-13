package com.yeditepe.service;

import com.yeditepe.entity.User;
import com.yeditepe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(com.yeditepe.dto.RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new org.springframework.dao.DataIntegrityViolationException("Kullanıcı adı zaten var!");
        }
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new org.springframework.dao.DataIntegrityViolationException("E-posta zaten var!");
        }

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .roles(registerRequest.getRoles() != null && !registerRequest.getRoles().isEmpty()
                        ? registerRequest.getRoles()
                        : java.util.Collections.singleton("USER"))
                .build();
        return userRepository.save(user);
    }

    public java.util.Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Kendi User nesnemizi veritabanından buluyoruz
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // 2. Kendi nesnemizi Spring'in anladığı UserDetails nesnesine dönüştürüyoruz
        String[] authorities = user.getRoles().stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .toArray(String[]::new);

        System.out.println("Loading user " + username + " with authorities: " + java.util.Arrays.toString(authorities));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
