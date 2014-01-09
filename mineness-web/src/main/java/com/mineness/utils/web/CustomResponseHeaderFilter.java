/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */
package com.mineness.utils.web;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Chris Tallent
 * Date: 6/29/12
 * Time: 3:50 PM
 */
public class CustomResponseHeaderFilter extends OncePerRequestFilter {

    private String accessControlAllowMethods;
    private String accessControlAllowHeaders;
    private String accessControlAllowCredentials;
    private String accessControlExposeHeaders;
    private String accessControlMaxAge;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // TODO - Should the origin be validated against specific URLS?
        String origin = request.getHeader("Origin");
        if (origin == null) {
            origin = request.getHeader("Host");
        }
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        if (accessControlAllowMethods != null) {
            response.setHeader("Access-Control-Allow-Methods", accessControlAllowMethods);
        }

        if (accessControlAllowHeaders != null) {
            response.setHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);
        }

        if (accessControlAllowCredentials != null) {
            response.setHeader("Access-Control-Allow-Credentials", accessControlAllowCredentials);
        }

        if (accessControlExposeHeaders != null) {
            response.setHeader("Access-Control-Expose-Headers", accessControlExposeHeaders);
        }

        if (accessControlMaxAge != null) {
            response.setHeader("Access-Control-Max-Age", accessControlMaxAge);
        }

        if ("POST".equals(request.getMethod()) && request.getContentType() == null) {
            request = new IECorsRequest(request);
        }

        filterChain.doFilter(request, response);
    }

    public void setAccessControlAllowMethods(String accessControlAllowMethods) {
        this.accessControlAllowMethods = accessControlAllowMethods;
    }

    public void setAccessControlAllowHeaders(String accessControlAllowHeaders) {
        this.accessControlAllowHeaders = accessControlAllowHeaders;
    }

    public void setAccessControlAllowCredentials(String accessControlAllowCredentials) {
        this.accessControlAllowCredentials = accessControlAllowCredentials;
    }

    private class IECorsRequest extends HttpServletRequestWrapper {

        /**
         * Constructs a request object wrapping the given request.
         *
         * @throws IllegalArgumentException if the request is null
         */
        public IECorsRequest(HttpServletRequest request)
        throws IOException {
            super(request);
        }

        @Override
        public String getContentType() {
            //return "application/x-www-form-urlencoded";
            return "text/plain";
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }
    }
}
