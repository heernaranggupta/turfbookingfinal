package com.Turfbooking.configuration;

import com.Turfbooking.repository.BusinessRepository;
import com.Turfbooking.repository.UserRepository;
import com.Turfbooking.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    private UserRepository userRepository;
    private BusinessRepository businessRepository;
    @Value("${jwt.secret.accessToken}")
    private String accessToken;
    @Value("${jwt.secret.refreshToken}")
    private String refreshToken;

    @Autowired
    public JwtRequestFilter(UserRepository userRepository, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        UserDetails username = null; //String username = null;
        String jwt = null;

        // JWT Token is in the form "Bearer token".
        // Remove Bearer word and get only token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = new org.springframework.security.core.userdetails.User(jwtTokenUtil.getUsernameFromToken(jwt, accessToken), "", jwtTokenUtil.getRolesFromToken(jwt, accessToken));
            } catch (IllegalArgumentException iae) {
                logger.error(iae + " Unable to get jwt token.");
            } catch (ExpiredJwtException eje) {
                logger.error(eje + " Jwt token expired.");
            }
        } else {
            logger.warn("Jwt token does not begin with Bearer string.");
        }


        if (username != null) {
            if (jwtTokenUtil.validateToken(jwt, username.getUsername(), accessToken)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, username.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                /*
                 * After setting the Authentication in the context,
                 * we specify that the current user is authenticated.
                 * So it passes the Spring Security Configurations successfully.
                 * */
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }
        }
        filterChain.doFilter(request,response);

}
}
