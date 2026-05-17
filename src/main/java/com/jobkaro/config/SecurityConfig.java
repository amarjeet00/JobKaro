package com.jobkaro.config;

import com.jobkaro.dao.UserDAO;
import com.jobkaro.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDAO userDAO;
    public SecurityConfig(UserDAO userDAO) { this.userDAO = userDAO; }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/api/jobs").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // disabled for simplicity in academic project
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            User u = userDAO.findByEmail(email);
            if (u == null) throw new UsernameNotFoundException("User not found: " + email);
            return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .roles(u.getRole().toUpperCase())
                .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Custom encoder that wraps our SHA-256+salt system
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence raw) {
                return com.jobkaro.util.HashUtil.hashPassword(raw.toString());
            }
            @Override
            public boolean matches(CharSequence raw, String encoded) {
                return com.jobkaro.util.HashUtil.verifyPassword(raw.toString(), encoded);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
