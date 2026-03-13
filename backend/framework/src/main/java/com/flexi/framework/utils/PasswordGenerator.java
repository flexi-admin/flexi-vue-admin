package com.flexi.framework.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = "123456";
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("Encoded password for '" + password + "': " + encodedPassword);
    }
}
