package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login", "/users/registration", "/users").permitAll()
                        .anyRequest().hasAnyAuthority("ROLE_TEST1", "ROLE_TEST2")
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/books", true))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        String encoderType = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encoderType, new BCryptPasswordEncoder(10));
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(encoderType, encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(encoders.get(encoderType));

        return passwordEncoder;
    }
}
