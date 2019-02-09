package com.fightnet.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fightnet.dataAccess.UserDAO;
import com.fightnet.models.AppUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.fightnet.security.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private final UserDAO userRepository;

    JWTAuthorizationFilter(final AuthenticationManager authManager, final UserDAO userRepository) {
        super(authManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest req,
                                    final HttpServletResponse res,
                                    final FilterChain chain) throws IOException, ServletException {
        final String header = req.getHeader(HEADER);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(getAuthentication(req));
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(HEADER);
        if (token != null) {
            final DecodedJWT decodedJWT;
            try {
                decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                        .build()
                        .verify(token.replace(TOKEN_PREFIX, ""));
            } catch (Exception e) {
                logger.info("Wrong token", e);
                return null;
            }
            final String username = decodedJWT.getSubject();
            if (username != null) {
                final AppUser user = userRepository.findByUsername(username);
                return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getRoles());
            }
        }
        return null;
    }
}