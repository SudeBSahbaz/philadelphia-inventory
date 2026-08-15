package com.philadelphia.inventory.security;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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


        /*
         * JSESSIONID ve XSRF cookie'lerinin
         * frontend-backend arasında gönderilebilmesi
         * için gerekli.
         */
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
                                        CookieCsrfTokenRepository
                                                .withHttpOnlyFalse()
                                )

                                .csrfTokenRequestHandler(
                                        new SpaCsrfTokenRequestHandler()
                                )

                                /*
                                 * Bu endpointler kullanıcı giriş yapmadan
                                 * kullanılabilmelidir.
                                 *
                                 * Login:
                                 * henüz session / token yok.
                                 *
                                 * Forgot-password:
                                 * kullanıcı zaten şifresini bilmiyor.
                                 *
                                 * Reset-password:
                                 * doğrulama reset token'ı üzerinden
                                 * yapılacak.
                                 */
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


                                // --------------------------------------------------
                                // GİRİŞ YAPMADAN ERİŞİLEBİLEN
                                // AUTH ENDPOINTLERİ
                                // --------------------------------------------------

                                .requestMatchers(
                                        "/api/auth/login",
                                        "/api/auth/forgot-password",
                                        "/api/auth/reset-password"
                                )
                                .permitAll()


                                // --------------------------------------------------
                                // GİRİŞ GEREKTİREN AUTH ENDPOINTLERİ
                                // --------------------------------------------------

                                .requestMatchers(
                                        "/api/auth/me",
                                        "/api/auth/change-password",
                                        "/api/auth/logout"
                                )
                                .authenticated()


                                // --------------------------------------------------
                                // KULLANICI YÖNETİMİ
                                // SADECE ADMIN
                                // --------------------------------------------------

                                .requestMatchers(
                                        "/api/users/**"
                                )
                                .hasRole(
                                        "ADMIN"
                                )


                                // --------------------------------------------------
                                // SİLİNMİŞ BULUNTULAR
                                // SADECE ADMIN
                                // --------------------------------------------------

                                .requestMatchers(
                                        "/api/artifacts/deleted/**"
                                )
                                .hasRole(
                                        "ADMIN"
                                )


                                // --------------------------------------------------
                                // BULUNTU GEÇMİŞİ
                                // ADMIN + CREW_MEMBER
                                // --------------------------------------------------

                                .requestMatchers(
                                        "/api/artifacts/*/history"
                                )
                                .hasAnyRole(
                                        "ADMIN",
                                        "CREW_MEMBER"
                                )


                                // --------------------------------------------------
                                // PUBLIC BULUNTULAR
                                // GİRİŞ YAPMIŞ HERKES
                                // --------------------------------------------------

                                .requestMatchers(
                                        "/api/artifacts/public"
                                )
                                .authenticated()


                                // --------------------------------------------------
                                // FOTOĞRAF ENDPOINTLERİ
                                // --------------------------------------------------
                                //
                                // Ayrıntılı rol ve visibility kontrolü
                                // ArtifactPhotoController içerisinde.
                                //

                                .requestMatchers(
                                        "/api/artifacts/*/photos",
                                        "/api/artifacts/*/photos/**",
                                        "/api/artifacts/photos/**"
                                )
                                .authenticated()


                                // --------------------------------------------------
                                // GENEL BULUNTU ENDPOINTLERİ
                                // --------------------------------------------------

                                .requestMatchers(
                                        "/api/artifacts/**"
                                )
                                .authenticated()


                                // --------------------------------------------------
                                // DİĞER API ENDPOINTLERİ
                                // --------------------------------------------------

                                .requestMatchers(
                                        "/api/**"
                                )
                                .authenticated()


                                // --------------------------------------------------
                                // API DIŞINDAKİLER
                                // --------------------------------------------------

                                .anyRequest()
                                .permitAll()
                )


                // --------------------------------------------------
                // FORM LOGIN KAPALI
                // --------------------------------------------------

                .formLogin(
                        form ->
                                form.disable()
                )


                // --------------------------------------------------
                // HTTP BASIC KAPALI
                // --------------------------------------------------

                .httpBasic(
                        basic ->
                                basic.disable()
                );


        return http.build();
    }


    // ==================================================
    // SPA CSRF TOKEN HANDLER
    // ==================================================

    private static final class SpaCsrfTokenRequestHandler
            implements CsrfTokenRequestHandler {

        private final CsrfTokenRequestHandler plain =
                new CsrfTokenRequestAttributeHandler();

        private final CsrfTokenRequestHandler xor =
                new XorCsrfTokenRequestAttributeHandler();


        @Override
        public void handle(
                HttpServletRequest request,
                HttpServletResponse response,
                Supplier<CsrfToken> csrfToken
        ) {

            /*
             * BREACH korumasını uygular.
             */
            this.xor.handle(
                    request,
                    response,
                    csrfToken
            );


            /*
             * Deferred token'ı yükler.
             *
             * Böylece Angular tarafından kullanılacak
             * XSRF-TOKEN cookie'si oluşturulur.
             */
            csrfToken.get();
        }


        @Override
        public String resolveCsrfTokenValue(
                HttpServletRequest request,
                CsrfToken csrfToken
        ) {

            String headerValue =
                    request.getHeader(
                            csrfToken.getHeaderName()
                    );


            /*
             * Angular X-XSRF-TOKEN header'ını
             * göndermişse plain handler kullan.
             */
            if (
                    StringUtils.hasText(
                            headerValue
                    )
            ) {

                return this.plain
                        .resolveCsrfTokenValue(
                                request,
                                csrfToken
                        );
            }


            /*
             * Header yoksa XOR handler'a dön.
             */
            return this.xor
                    .resolveCsrfTokenValue(
                            request,
                            csrfToken
                    );
        }
    }
}