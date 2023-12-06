package ru.putilin.cloud_storage.securityconfiguration;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.putilin.cloud_storage.dao.TokenDAO;
import ru.putilin.cloud_storage.entity.JWTToken;
import ru.putilin.cloud_storage.exception.NotAuthorizedException;
import ru.putilin.cloud_storage.service.UserServiceImpl;

import java.io.IOException;
import java.util.Optional;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserServiceImpl userServiceImpl;
    private final TokenDAO tokenDAO;


    public JWTFilter(JWTUtil jwtUtil, UserServiceImpl userServiceImpl, TokenDAO tokenDAO) {
        this.jwtUtil = jwtUtil;
        this.userServiceImpl = userServiceImpl;
        this.tokenDAO = tokenDAO;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("auth-token");

        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            Optional<JWTToken> tokenFromDB = tokenDAO.findByAuthToken(jwt);
            if (jwt.isBlank() || tokenFromDB.isEmpty()) {
                throw new NotAuthorizedException("Invalid JWT Token");
            } else {
                try {
                    String userName = jwtUtil.validateTokenAndExtractUser(jwt);
                    UserDetails userDetails = userServiceImpl.loadUserByUsername(userName);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                            userDetails.getAuthorities());
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (JWTVerificationException e) {
                    throw new NotAuthorizedException("Verification is denied");
                }

            }
        }
        filterChain.doFilter(request, response);
    }
}
