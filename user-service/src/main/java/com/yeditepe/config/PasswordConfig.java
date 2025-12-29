package com.yeditepe.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {



    @Bean
    public PasswordEncoder passwordEncoder() {
        // Şifreleri güvenli bir şekilde hashlemek için BCrypt kullanıyoruz.
        return new BCryptPasswordEncoder();
    }
}
