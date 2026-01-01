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



    public User registerUser(User user) {
        // Şifreyi veritabanına kaydetmeden önce şifreliyoruz!
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Kendi User nesnemizi veritabanından buluyoruz
        com.yeditepe.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // 2. Kendi nesnemizi Spring'in anladığı UserDetails nesnesine dönüştürüyoruz
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(role -> "ROLE_" + role).toArray(String[]::new))
                .build();
    }





}
