package com.yeditepe.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final AuthTokenFilter authTokenFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/api/users/{id}/validate").permitAll()
                                                .requestMatchers("/error").permitAll()
                                                .requestMatchers("/api/users/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                // JWT kullanacağımız için oturum yönetimini STATELESS yapıyoruz
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                // JWT filterini UsernamePasswordAuthenticationFilter'tan önce ekleyelim
                http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
