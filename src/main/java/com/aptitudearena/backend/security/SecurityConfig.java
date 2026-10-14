package com.aptitudearena.backend.security;

import com.aptitudearena.backend.service.AppUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   AppUserService appUserService) throws Exception {

        http
            // Disable CSRF for REST API
            .csrf(csrf -> csrf.disable())

            // Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .addFilterBefore(new JwtAuthenticationFilter(jwtService, appUserService),
                    UsernamePasswordAuthenticationFilter.class)

            // API authorization
            .authorizeHttpRequests(auth -> auth

                // Public authentication APIs
                .requestMatchers("/api/auth/**").permitAll()

                // Health API
                .requestMatchers("/api/health").permitAll()

                // Public exam/question/subject GET APIs
                .requestMatchers(HttpMethod.GET, "/api/exams", "/api/exams/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/subjects", "/api/subjects/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/questions", "/api/questions/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/papers", "/api/papers/**").permitAll()

                // Everything else requires authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
            "https://aptitude-arena.ai.studio",
            "http://localhost:3000",
            "http://localhost:5173"
        ));

        configuration.setAllowedMethods(List.of(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}