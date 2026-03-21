package io.github.zmxckj.flexiadmin.security;

import io.github.zmxckj.flexiadmin.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AppIdAuthenticationFilter appIdAuthenticationFilter;
    private final List<SecurityPermitPathProvider> permitPathProviders;

    @Value("${flexi.security.permit-paths:}")
    private String permitPaths;

    @Autowired
    public SecurityConfig(JwtUtils jwtUtils, JwtAuthenticationFilter jwtAuthenticationFilter, AppIdAuthenticationFilter appIdAuthenticationFilter, 
                         @Autowired(required = false) List<SecurityPermitPathProvider> permitPathProviders) {
        this.jwtUtils = jwtUtils;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.appIdAuthenticationFilter = appIdAuthenticationFilter;
        this.permitPathProviders = permitPathProviders != null ? permitPathProviders : new ArrayList<>();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> {
                // 添加默认的开放路径
                authorize.requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/images/**").permitAll();
                
                // 添加配置的开放路径
                if (StringUtils.hasText(permitPaths)) {
                    String[] paths = permitPaths.split(",");
                    for (String path : paths) {
                        if (StringUtils.hasText(path)) {
                            authorize.requestMatchers(path.trim()).permitAll();
                        }
                    }
                }
                
                // 添加通过 SecurityPermitPathProvider 提供的开放路径
                for (SecurityPermitPathProvider provider : permitPathProviders) {
                    List<String> paths = provider.getPermitPaths();
                    if (paths != null && !paths.isEmpty()) {
                        for (String path : paths) {
                            if (StringUtils.hasText(path)) {
                                authorize.requestMatchers(path.trim()).permitAll();
                            }
                        }
                    }
                }
                
                // 其他请求需要认证
                authorize.anyRequest().authenticated();
            })
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable());

        // 先添加AppId过滤器，再添加JWT过滤器
        http.addFilterBefore(appIdAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
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