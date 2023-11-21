package ru.putilin.cloud_storage.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.putilin.cloud_storage.dao.TokenDAO;
import ru.putilin.cloud_storage.entity.JWTToken;

@Service
public class LogoutService implements LogoutHandler {

    private final TokenDAO tokenDAO;

    public LogoutService(TokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    @Transactional
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("auth-token");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return;
        String jwt = authHeader.substring(7);
        tokenDAO.deleteJWTTokenByAuthToken(jwt);
        System.out.println("PASSED");
//        JWTToken storedToken = tokenDAO.findByAuthToken(jwt).orElseThrow();
//        storedToken.setExpired(true);
//        tokenDAO.save(storedToken);
        SecurityContextHolder.clearContext();

    }
}
