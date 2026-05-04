package lggur.otp_service.config;

import jakarta.servlet.http.HttpServletResponse;
import lggur.otp_service.service.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JwtFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        if (path.startsWith("/auth")) {
            log.debug("Skipping JWT filter for auth path: {}", path);
            chain.doFilter(request, response);
            return;
        }

        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            log.warn("Unauthorized request to {}: missing or invalid Authorization header", path);
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = header.substring(7);

        try {
            Long userId = jwtService.extractUserId(token);
            req.setAttribute("userId", userId);
            log.debug("JWT authenticated: userId={}, path={}", userId, path);

        } catch (Exception e) {
            log.warn("Invalid JWT for path {}: {}", path, e.getMessage());
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Invalid JWT");
            return;
        }

        chain.doFilter(request, response);
    }
}