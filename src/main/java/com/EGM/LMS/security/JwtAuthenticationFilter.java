package com.EGM.LMS.security;

import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.repository.UserSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtDecoder jwtDecoder;
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;
    private final RbacAuthorityService rbacAuthorityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = header.substring(7);
        try {
            Jwt jwt = jwtDecoder.decode(token);
            var sessionOpt = userSessionRepository.findByTokenAndIsActiveTrue(token);
            if (sessionOpt.isEmpty()) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }
            var session = sessionOpt.get();
            if (session.getExpiresAt() != null && session.getExpiresAt().isBefore(LocalDateTime.now())) {
                session.setActive(false);
                userSessionRepository.save(session);
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }
            session.setLastActiveAt(LocalDateTime.now());
            userSessionRepository.save(session);

            var email = jwt.getSubject();
                var user = userRepository.findByEmail(email).orElse(null);
                var authorities = rbacAuthorityService.buildAuthorities(user);

            var authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException ignored) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
