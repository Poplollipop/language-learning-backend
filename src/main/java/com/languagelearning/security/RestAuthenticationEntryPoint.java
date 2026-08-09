package com.languagelearning.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

// Without this, Spring Security's default entry point returns 403 for missing/invalid
// JWTs, indistinguishable from a role-based 403 (e.g. STUDENT hitting a TEACHER-only
// endpoint). Clients need 401 specifically to know "log the user out", not "no permission".
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String path = request.getRequestURI().replace("\"", "'");
        response.getWriter().write(
            "{\"timestamp\":\"" + Instant.now() + "\",\"status\":401,\"error\":\"Unauthorized\","
                + "\"message\":\"Full authentication is required to access this resource\","
                + "\"path\":\"" + path + "\"}");
    }
}
