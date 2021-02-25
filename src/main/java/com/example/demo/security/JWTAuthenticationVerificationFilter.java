package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.example.demo.security.SecurityConstant.HEADER_STRING;
import static com.example.demo.security.SecurityConstant.SECRET_KEY;
import static com.example.demo.security.SecurityConstant.TOKEN_PREFIX;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

public class JWTAuthenticationVerificationFilter extends BasicAuthenticationFilter {

    private final Logger logger = getLogger(JWTAuthenticationVerificationFilter.class);

    public JWTAuthenticationVerificationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token == null) {
            return null;
        }
        String user = JWT.require(HMAC512(SECRET_KEY.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();
        if (user == null) {
            return null;
        }
        return new UsernamePasswordAuthenticationToken(user, null, emptyList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(HEADER_STRING);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            chain.doFilter(request, response);
        } catch (SignatureVerificationException signatureVerificationException) {
            this.logger.error("The token signature is invalid. {}", signatureVerificationException.getMessage());
        }
    }

}