package com.orange.discobole.productcatalog.catalog.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CspFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        if (!path.startsWith("/swagger-ui")
                && !path.startsWith("/v3/api-docs")
                && !path.startsWith("/swagger-ui.html")
                && !path.startsWith("/oauth2-redirect.html")) {

            res.setHeader(
                    "Content-Security-Policy",
                    "default-src 'self'"
            );
        }

        chain.doFilter(request, response);
    }
}
