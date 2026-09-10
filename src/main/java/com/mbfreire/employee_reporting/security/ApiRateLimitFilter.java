package com.mbfreire.employee_reporting.security;

import com.mbfreire.employee_reporting.dto.response.ErrorResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ApiRateLimitFilter
        extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper mapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        RateLimitRule rule =
                findRule(request);

        if (rule == null) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String client =
                resolveClientIdentifier(request);

        String key =
                rule.name()
                        + ":"
                        + client;

        boolean allowed =
                rateLimitService.allow(
                        key,
                        rule.maxRequests(),
                        rule.window()
                );

        if (!allowed) {

            response.setStatus(429);

            response.setContentType(
                    "application/json;charset=UTF-8"
            );

            response.setHeader(
                    "Retry-After",
                    String.valueOf(
                            Math.max(
                                    1,
                                    rule.window().toSeconds()
                            )
                    )
            );

            ErrorResponseDTO error =
                    new ErrorResponseDTO(
                            429,
                            "Muitas tentativas. Aguarde antes de tentar novamente.",
                            LocalDateTime.now()
                    );

            response.getWriter().write(
                    mapper.writeValueAsString(error)
            );

            return;
        }

        filterChain.doFilter(
                request,
                response
        );
    }

    private RateLimitRule findRule(
            HttpServletRequest request
    ) {

        String method =
                request.getMethod();

        String uri =
                request.getRequestURI();

        if (HttpMethod.POST.matches(method)
                && uri.equals("/api/auth/login")) {

            return new RateLimitRule(
                    "login",
                    5,
                    Duration.ofMinutes(1)
            );
        }

        if (HttpMethod.POST.matches(method)
                && uri.equals(
                "/api/auth/forgot-password"
        )) {

            return new RateLimitRule(
                    "forgot-password",
                    3,
                    Duration.ofMinutes(15)
            );
        }

        if (HttpMethod.POST.matches(method)
                && uri.equals(
                "/api/auth/reset-password"
        )) {

            return new RateLimitRule(
                    "reset-password",
                    5,
                    Duration.ofMinutes(15)
            );
        }

        if (HttpMethod.GET.matches(method)
                && uri.equals(
                "/api/reports/consult"
        )) {

            return new RateLimitRule(
                    "report-consult",
                    20,
                    Duration.ofMinutes(1)
            );
        }

        if (HttpMethod.POST.matches(method)
                && uri.equals("/api/reports")) {

            return new RateLimitRule(
                    "report-create",
                    10,
                    Duration.ofHours(1)
            );
        }

        if (HttpMethod.POST.matches(method)
                && uri.matches(
                "^/api/reports/[^/]+/attachments$"
        )) {

            return new RateLimitRule(
                    "attachment-upload",
                    20,
                    Duration.ofHours(1)
            );
        }

        if (HttpMethod.GET.matches(method)
                && uri.matches(
                "^/api/reports/admin/[^/]+/attachments/[^/]+$"
        )) {

            return new RateLimitRule(
                    "attachment-download",
                    120,
                    Duration.ofMinutes(1)
            );
        }

        return null;
    }

    private String resolveClientIdentifier(
            HttpServletRequest request
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal()
                instanceof UserDetailsImpl userDetails) {

            return "user:"
                    + userDetails.getUser().getId()
                    + ":ip:"
                    + resolveIp(request);
        }

        return "ip:"
                + resolveIp(request);
    }

    private String resolveIp(
            HttpServletRequest request
    ) {

        String remote =
                request.getRemoteAddr();

        if ("127.0.0.1".equals(remote)
                || "0:0:0:0:0:0:0:1".equals(remote)
                || "::1".equals(remote)) {

            String forwarded =
                    request.getHeader(
                            "X-Forwarded-For"
                    );

            if (forwarded != null
                    && !forwarded.isBlank()) {

                return forwarded
                        .split(",")[0]
                        .trim();
            }
        }

        return remote;
    }

    private record RateLimitRule(
            String name,
            int maxRequests,
            Duration window
    ) {
    }
}