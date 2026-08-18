package com.philadelphia.inventory.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {


    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;


    // ==================================================
    // PASSWORD ENCODER
    // ==================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ==================================================
    // AUTHENTICATION MANAGER
    // ==================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration
                .getAuthenticationManager();
    }


    // ==================================================
    // CORS
    // ==================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        configuration.setAllowedOrigins(
                List.of(
                        allowedOrigin
                )
        );


        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );


        configuration.setAllowedHeaders(
                List.of("*")
        );


        configuration.setExposedHeaders(
                List.of(
                        "X-XSRF-TOKEN"
                )
        );


        configuration.setAllowCredentials(
                true
        );


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }


    // ==================================================
    // SECURITY FILTER CHAIN
    // ==================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        HttpSessionCsrfTokenRepository csrfTokenRepository =
                new HttpSessionCsrfTokenRepository();

        csrfTokenRepository.setHeaderName(
                "X-XSRF-TOKEN"
        );


        http

                // --------------------------------------------------
                // CORS
                // --------------------------------------------------

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )


                // --------------------------------------------------
                // CSRF
                // --------------------------------------------------

                .csrf(csrf ->
                        csrf

                                .csrfTokenRepository(
                                        csrfTokenRepository
                                )

                                .csrfTokenRequestHandler(
                                        new CsrfTokenRequestAttributeHandler()
                                )

                                .ignoringRequestMatchers(
                                        "/api/auth/login",
                                        "/api/auth/forgot-password",
                                        "/api/auth/reset-password"
                                )
                )


                // --------------------------------------------------
                // AUTHORIZATION
                // --------------------------------------------------

                .authorizeHttpRequests(auth ->
                        auth

                                // ----------------------------------
                                // LOGIN / PASSWORD RESET
                                // ----------------------------------

                                .requestMatchers(
                                        "/api/auth/login",
                                        "/api/auth/forgot-password",
                                        "/api/auth/reset-password"
                                )
                                .permitAll()


                                // ----------------------------------
                                // AUTHENTICATED AUTH ENDPOINTS
                                // ----------------------------------

                                .requestMatchers(
                                        "/api/auth/me",
                                        "/api/auth/change-password",
                                        "/api/auth/logout"
                                )
                                .authenticated()


                                // ----------------------------------
                                // USER MANAGEMENT
                                // ONLY ADMIN
                                // ----------------------------------

                                .requestMatchers(
                                        "/api/users/**"
                                )
                                .hasRole(
                                        "ADMIN"
                                )


                                // ----------------------------------
                                // DELETED ARTIFACTS
                                // ADMIN + CREW_MEMBER
                                // ----------------------------------

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/artifacts/deleted"
                                )
                                .hasAnyRole(
                                        "ADMIN",
                                        "CREW_MEMBER"
                                )


                                // ----------------------------------
                                // DELETE ARTIFACT
                                // ADMIN + CREW_MEMBER
                                // ----------------------------------

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/artifacts/*"
                                )
                                .hasAnyRole(
                                        "ADMIN",
                                        "CREW_MEMBER"
                                )


                                // ----------------------------------
                                // RESTORE ARTIFACT
                                // ADMIN + CREW_MEMBER
                                // ----------------------------------

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/artifacts/*/restore"
                                )
                                .hasAnyRole(
                                        "ADMIN",
                                        "CREW_MEMBER"
                                )


                                // ----------------------------------
                                // ARTIFACT HISTORY
                                // ADMIN + CREW_MEMBER
                                // ----------------------------------

                                .requestMatchers(
                                        "/api/artifacts/*/history"
                                )
                                .hasAnyRole(
                                        "ADMIN",
                                        "CREW_MEMBER"
                                )


                                // ----------------------------------
                                // ARTIFACT PHOTOS
                                // ----------------------------------

                                .requestMatchers(
                                        "/api/artifacts/*/photos",
                                        "/api/artifacts/*/photos/**",
                                        "/api/artifacts/photos/**"
                                )
                                .authenticated()


                                // ----------------------------------
                                // ALL OTHER ARTIFACT ENDPOINTS
                                // ----------------------------------

                                .requestMatchers(
                                        "/api/artifacts/**"
                                )
                                .authenticated()


                                // ----------------------------------
                                // OTHER API ENDPOINTS
                                // ----------------------------------

                                .requestMatchers(
                                        "/api/**"
                                )
                                .authenticated()


                                // ----------------------------------
                                // NON-API
                                // ----------------------------------

                                .anyRequest()
                                .permitAll()
                )


                // --------------------------------------------------
                // FORM LOGIN DISABLED
                // --------------------------------------------------

                .formLogin(
                        form ->
                                form.disable()
                )


                // --------------------------------------------------
                // HTTP BASIC DISABLED
                // --------------------------------------------------

                .httpBasic(
                        basic ->
                                basic.disable()
                );


        return http.build();
    }
}