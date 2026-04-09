package ru.amin.Rest.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.amin.Rest.security.JWTUtil;
import ru.amin.Rest.services.UsersDetailsService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.io.IOException;


@Component
public class JWTFilter extends OncePerRequestFilter {
    private final UsersDetailsService usersDetailsService;
    private final JWTUtil jwtUtil;

    public JWTFilter(UsersDetailsService usersDetailsService, JWTUtil jwtUtil) {
        this.usersDetailsService = usersDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String jwt = authHeader.substring(7);

            if (jwt.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid JWT Token");
                return;
            }

            try {
                String username = jwtUtil.validateTokenAndRetrieveClaim(jwt);

                UserDetails userDetails =
                        usersDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);

            } catch (JWTVerificationException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid JWT Token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
