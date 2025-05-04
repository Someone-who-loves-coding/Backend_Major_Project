package com.rest.backend_rest.interceptors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String path = request.getRequestURI();
        return path.equals("/api/v1/auth/signup/") || path.equals("/api/v1/auth/login/");
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        LocalDateTime requestTime = LocalDateTime.now();

        try{
            filterChain.doFilter(request, response);
        } finally {
            // Log request details
            log.info("Request: [{}] {} {} at [{}]",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getProtocol(),
                    requestTime
            );

            // Log response details
            log.info("Response: [{}] for [{} {}] at [{}]",
                    response.getStatus(),
                    request.getMethod(),
                    request.getRequestURI(),
                    LocalDateTime.now()
            );
        }
    }
}
