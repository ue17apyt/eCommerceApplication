package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.example.demo.security.SecurityConstant.EXPIRATION_TIME;
import static com.example.demo.security.SecurityConstant.HEADER_STRING;
import static com.example.demo.security.SecurityConstant.SECRET_KEY;
import static com.example.demo.security.SecurityConstant.TOKEN_PREFIX;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Logger logger = getLogger(JWTAuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            this.logger.info("Attempt authentication for user {}", user.getUsername());
            return this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), emptyList())
            );
        } catch (IOException ioException) {
            this.logger.error("Failure to attempt authentication: {}", ioException.getMessage());
            throw new RuntimeException(ioException);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication
    ) {
        String token = JWT.create()
                .withSubject(
                        (
                                (org.springframework.security.core.userdetails.User) authentication.getPrincipal()
                        ).getUsername()
                )
                .withExpiresAt(new Date(currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET_KEY.getBytes()));
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        this.logger.info(
                "User {} is authenticated and JSON Web Token is issued",
                ((User) authentication.getPrincipal()).getUsername()
        );
    }

}