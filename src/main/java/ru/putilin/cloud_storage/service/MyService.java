package ru.putilin.cloud_storage.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.putilin.cloud_storage.dao.TokenDAO;
import ru.putilin.cloud_storage.dto.TokenDTO;
import ru.putilin.cloud_storage.dto.UserDTO;
import ru.putilin.cloud_storage.entity.JWTToken;
import ru.putilin.cloud_storage.entity.User;
import ru.putilin.cloud_storage.securityconfiguration.JWTUtil;

@Service
@Transactional
public class MyService {

    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userServiceImpl;
    private final TokenDAO tokenDAO;

    public MyService(ModelMapper modelMapper, JWTUtil jwtUtil, AuthenticationManager authenticationManager, UserServiceImpl userServiceImpl, TokenDAO tokenDAO) {
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userServiceImpl = userServiceImpl;
        this.tokenDAO = tokenDAO;
    }

    public ResponseEntity<TokenDTO> login(UserDTO userDTO) {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDTO.getUsername(),
                userDTO.getPassword());
        authenticationManager.authenticate(authToken);

        User user = convertUserDtoToUser(userDTO);
        user.setId(userServiceImpl.loadUserByUsername(user.getUsername()).getId());

        JWTToken token = new JWTToken();
        token.setToken(jwtUtil.generateToken(user.getUsername()));
        token.setUser(user);
        if (tokenDAO.existsJWTTokenByUserId(user.getId())) {
            tokenDAO.update(token.getAuthToken(), user.getId());
        } else
            tokenDAO.save(token);

        TokenDTO tokenDTO = convertTokenToTokenDTO(token);

        return ResponseEntity.ok(tokenDTO);
    }


    public TokenDTO convertTokenToTokenDTO(JWTToken token) {
        return this.modelMapper.map(token, TokenDTO.class);
    }

    public User convertUserDtoToUser(UserDTO userDTO) {
        return this.modelMapper.map(userDTO, User.class );
    }
}
