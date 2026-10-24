package com.edad.hardtrack.security;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * CONFIGURACIÓN DE SEGURIDAD - Spring Security con autenticación JDBC
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery("select username, password, enable from users where username = ?");
        manager.setAuthoritiesByUsernameQuery("select u.username, a.authority from authorities a join users u on u.id = a.id_user where u.username = ?");
        return manager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/devices.html", "/orders.html",
                                        "/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/devices", "/api/devices/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/device-types", "/api/device-types/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/device-types").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/devices/order").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/devices").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/devices").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/devices/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
