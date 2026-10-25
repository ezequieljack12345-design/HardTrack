package com.edad.hardtrack.security;

import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private record UserPassword(int id, String password) {
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationRunner migrateUserPasswords(
            JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        return args -> {
            Long passwordColumnLength = jdbcTemplate.queryForObject(
                    """
                            SELECT CHARACTER_MAXIMUM_LENGTH
                            FROM INFORMATION_SCHEMA.COLUMNS
                            WHERE TABLE_SCHEMA = DATABASE()
                              AND TABLE_NAME = 'users'
                              AND COLUMN_NAME = 'password'
                            """,
                    Long.class);
            if (passwordColumnLength == null) {
                throw new IllegalStateException("No se encontró la columna users.password");
            }
            if (passwordColumnLength < 60) {
                jdbcTemplate.execute("ALTER TABLE users MODIFY password VARCHAR(60)");
            }

            List<UserPassword> users = jdbcTemplate.query(
                    "SELECT id, password FROM users",
                    (resultSet, rowNum) -> new UserPassword(resultSet.getInt("id"), resultSet.getString("password")));

            int migratedCount = 0;
            for (UserPassword user : users) {
                if (user.password() == null) {
                    throw new IllegalStateException("El usuario con id " + user.id() + " no tiene contraseña");
                }
                if (!user.password().matches("\\A\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}\\z")) {
                    jdbcTemplate.update("UPDATE users SET password = ? WHERE id = ?",
                            passwordEncoder.encode(user.password()), user.id());
                    migratedCount++;
                }
            }
            if (migratedCount > 0) {
                logger.info("Se migraron {} contraseñas a BCrypt", migratedCount);
            }
        };
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
