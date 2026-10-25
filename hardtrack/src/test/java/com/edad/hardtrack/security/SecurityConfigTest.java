package com.edad.hardtrack.security;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecurityConfigTest {

    @Test
    void passwordEncoder_shouldStoreBcryptHashesAndMatchOriginalPassword() {
        PasswordEncoder passwordEncoder = new SecurityConfig().passwordEncoder();
        String password = "admin";

        String encodedPassword = passwordEncoder.encode(password);

        assertNotEquals(password, encodedPassword);
        assertTrue(passwordEncoder.matches(password, encodedPassword));
    }
}
