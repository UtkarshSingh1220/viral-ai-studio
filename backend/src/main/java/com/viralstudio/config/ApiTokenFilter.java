package com.viralstudio.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Shared bearer-style token gate for this single-creator MVP.
 * Use a long random APP_API_TOKEN from a secret manager; this is not multi-user identity.
 */
@Component
public class ApiTokenFilter extends OncePerRequestFilter {
    private final String configuredToken;

    public ApiTokenFilter(@Value("${app.security.api-token:}") String configuredToken) {
        this.configuredToken = configuredToken == null ? "" : configuredToken.trim();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/") || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (configuredToken.isEmpty()) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "API access is not configured. Set APP_API_TOKEN before starting the backend.");
            return;
        }
        String supplied = request.getHeader("X-App-Token");
        if (supplied == null || !MessageDigest.isEqual(
                configuredToken.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8))) {
            response.setHeader("WWW-Authenticate", "ApiToken");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Valid X-App-Token header required.");
            return;
        }
        chain.doFilter(request, response);
    }
}
