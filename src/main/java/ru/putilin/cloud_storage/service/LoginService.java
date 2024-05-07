package ru.putilin.cloud_storage.service;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.putilin.cloud_storage.dao.TokenDAO;
import ru.putilin.cloud_storage.dao.UserDAO;
import ru.putilin.cloud_storage.dto.TokenDTO;
import ru.putilin.cloud_storage.dto.UserDTO;
import ru.putilin.cloud_storage.entity.JWTToken;
import ru.putilin.cloud_storage.entity.User;
import ru.putilin.cloud_storage.exception.IncorrectInputException;
import ru.putilin.cloud_storage.exception.NotAuthorizedException;
import ru.putilin.cloud_storage.securityconfiguration.JWTUtil;

@Service
public class LoginService {

    private final static Logger LOG = LoggerFactory.getLogger(LoginService.class);

    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenDAO tokenDAO;

    private final UserDAO userDAO;

    public LoginService(ModelMapper modelMapper, JWTUtil jwtUtil, AuthenticationManager authenticationManager, TokenDAO tokenDAO, UserDAO userDAO) {
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.tokenDAO = tokenDAO;
        this.userDAO = userDAO;
    }


    @Transactional
    public TokenDTO login(UserDTO userDTO) {

        String password = new BCryptPasswordEncoder().encode("password2");
        System.out.println(password);
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDTO.getLogin(),
                    userDTO.getPassword());
            authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            LOG.warn("Invalid username of password");
            throw new NotAuthorizedException("Invalid username or password");
        }
        User user = new User();
        user.setEmail(userDTO.getLogin());
        user.setId(userDAO.findByEmail(user.getEmail())
                .orElseThrow(() -> new IncorrectInputException("User has not been found"))
                .getId());
        user.setPassword(password);

        JWTToken token = new JWTToken();
        token.setToken(jwtUtil.generateToken(user.getEmail()));
        token.setUser(user);
        if (tokenDAO.existsJWTTokenByUserId(user.getId())) {
            tokenDAO.update(token.getAuthToken(), user.getId());
            LOG.info("Token is updated");
        } else {
            tokenDAO.save(token);
            LOG.info("Token is saved");
        }
        LOG.info("User {} authenticated", user.getEmail());
        return convertTokenToTokenDTO(token);
    }

    public TokenDTO convertTokenToTokenDTO(JWTToken token) {
        return this.modelMapper.map(token, TokenDTO.class);
    }

}
