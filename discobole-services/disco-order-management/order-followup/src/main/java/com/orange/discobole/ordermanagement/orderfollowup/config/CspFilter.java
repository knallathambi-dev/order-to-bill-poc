package com.orange.discobole.ordermanagement.orderfollowup.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CspFilter implements Filter {

    private static final String CSP_HEADER = "Content-Security-Policy";
    private static final String CSP_SWAGGER = "default-src 'self' 'unsafe-inline' 'unsafe-eval' data:";
    private static final String CSP_STRICT = "default-src 'self'";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        if (!res.containsHeader(CSP_HEADER)) {
            res.setHeader(CSP_HEADER, getCspPolicy(req.getRequestURI()));
        }

        chain.doFilter(request, response);
    }

    private String getCspPolicy(String requestUri) {
        if (requestUri == null) {
            return CSP_STRICT;
        }
        return isSwaggerEndpoint(requestUri) ? CSP_SWAGGER : CSP_STRICT;
    }

    private boolean isSwaggerEndpoint(String requestUri) {
        return requestUri.startsWith("/swagger-ui") || requestUri.startsWith("/v3/api-docs");
    }
}