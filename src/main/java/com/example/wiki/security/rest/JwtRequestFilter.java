package com.example.wiki.security.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.sasl.AuthenticationException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.wiki.api.user.AuthController;
import com.example.wiki.configuration.Constants;
import com.example.wiki.service.AuthService;
import com.example.wiki.util.JwtTokenUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ConditionalOnBean(SecurityRestConfiguration.class)
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final List<String> whitelist;
    private final UserDetailsService userDetailsService;
    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtRequestFilter(UserDetailsService userDetailsService, AuthService authService, JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.authService = authService;
        this.jwtTokenUtil = jwtTokenUtil;
        whitelist = new ArrayList<>(SecurityRestConfiguration.WHITELISTED_URLS);
        whitelist.addAll(List.of(
                AuthController.URL + Constants.LOGIN_URL,
                AuthController.URL + Constants.SIGNUP_URL));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final String token = JwtTokenUtil.getTokenFromRequest(request);

        if (whitelist.stream().noneMatch(requestURI::startsWith) && StringUtils.hasText(token)) {

            if (authService.isTokenBlacklisted(token)) {
                throw new AuthenticationException("Token has been invalidated");
            }

            final String username = jwtTokenUtil.extractUsername(token);

            if (username != null) {
                final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (!jwtTokenUtil.validateToken(token, userDetails)) {
                    throw new AuthenticationException("Invalid token");
                }

                final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
    }
}
