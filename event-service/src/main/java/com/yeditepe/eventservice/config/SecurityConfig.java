package com.yeditepe.eventservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.annotation.PostConstruct;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final AuthTokenFilter authTokenFilter;

        public SecurityConfig(AuthTokenFilter authTokenFilter) {
                this.authTokenFilter = authTokenFilter;
        }

        @PostConstruct
        public void init() {
                System.out.println("SecurityConfig initialized in Event Service");
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/events/**")
                                                .permitAll()
                                                .requestMatchers("/api/events/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .httpBasic(AbstractHttpConfigurer::disable)
                                .formLogin(AbstractHttpConfigurer::disable);

                http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
