package io.github.zmxckj.flexiadmin.security;

import io.github.zmxckj.flexiadmin.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtUtils jwtUtils, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtUtils = jwtUtils;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // 提供一个空的 UserDetailsService 实现，以禁用默认的 inMemoryUserDetailsManager
        return username -> {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}