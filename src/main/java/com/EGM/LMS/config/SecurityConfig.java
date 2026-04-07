package com.EGM.LMS.config;

import com.EGM.LMS.security.JwtAuthenticationFilter;
import com.EGM.LMS.security.OAuth2LoginSuccessHandler;
import com.EGM.LMS.security.RbacAuthorityService;
import com.EGM.LMS.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final UserSessionRepository userSessionRepository;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.oauth2.redirect-uri}")
    private String oauthRedirectUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**", "/oauth2/**", "/login/oauth2/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/courses/**", "/api/course-categories/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/course-sections/**", "/api/lessons/**", "/api/reviews/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/course-outcomes/**", "/api/course-requirements/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/lesson-discussions/**", "/api/discussion-replies/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/payments/chapa/callback").permitAll()
                    .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                    .successHandler(oAuth2LoginSuccessHandler)
                    .failureHandler((request, response, exception) -> {
                        String redirect = buildOAuthFailureRedirect(exception);
                        response.sendRedirect(redirect);
                    })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtDecoder jwtDecoder, RbacAuthorityService rbacAuthorityService, com.EGM.LMS.repository.UserRepository userRepository) {
        return new JwtAuthenticationFilter(jwtDecoder, userSessionRepository, userRepository, rbacAuthorityService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        var origins = List.of(allowedOrigins.split(",")).stream()
            .map(String::trim)
            .filter(origin -> !origin.isEmpty())
            .toList();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private String buildOAuthFailureRedirect(AuthenticationException exception) {
        String message = exception != null && exception.getMessage() != null
            ? exception.getMessage()
            : "OAuth login failed";
        return UriComponentsBuilder.fromUriString(oauthRedirectUri)
            .queryParam("oauthError", message)
            .build()
            .toUriString();
    }

}
