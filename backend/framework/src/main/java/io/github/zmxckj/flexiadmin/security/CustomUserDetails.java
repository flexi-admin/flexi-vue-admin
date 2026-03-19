package io.github.zmxckj.flexiadmin.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    
    private Long id;
    private String username;
    private List<String> authorities;
    private List<String> roles;
    
    public CustomUserDetails(Long id, String username, List<String> authorities, List<String> roles) {
        this.id = id;
        this.username = username;
        this.authorities = authorities;
        this.roles = roles;
    }
    
    public Long getId() {
        return id;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public List<String> getAuthorityStrings() {
        return authorities;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (authorities != null) {
            for (String authority : authorities) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        return grantedAuthorities;
    }
    
    @Override
    public String getPassword() {
        return null;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}