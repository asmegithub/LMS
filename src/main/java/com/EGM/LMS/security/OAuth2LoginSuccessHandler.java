package com.EGM.LMS.security;

import com.EGM.LMS.dto.auth.AuthResponse;
import com.EGM.LMS.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthService authService;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof OAuth2User oauthUser)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 user not found");
            return;
        }

        var email = getStringAttribute(oauthUser, "email");
        var firstName = getStringAttribute(oauthUser, "given_name");
        var lastName = getStringAttribute(oauthUser, "family_name");
        var picture = getStringAttribute(oauthUser, "picture");

        AuthResponse authResponse = authService.oauthLogin(
                email,
                firstName,
                lastName,
                picture,
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        var redirect = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", authResponse.getAccessToken())
                .queryParam("refreshToken", authResponse.getRefreshToken())
                .build()
                .toUriString();

        response.sendRedirect(redirect);
    }

    private String getStringAttribute(OAuth2User oauthUser, String key) {
        var value = oauthUser.getAttribute(key);
        return value != null ? value.toString() : null;
    }
}
