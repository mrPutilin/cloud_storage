package ru.putilin.cloud_storage.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.putilin.cloud_storage.dao.UserDAO;
import ru.putilin.cloud_storage.entity.User;
import ru.putilin.cloud_storage.exception.IncorrectInputException;
import ru.putilin.cloud_storage.securityconfiguration.UserDetailsImpl;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserDetailsService {

    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetailsImpl loadUserByUsername(String username) {
        Optional<User> user = userDAO.findByEmail(username);
        if (user.isEmpty()) {
            throw new IncorrectInputException("User has not been found");
        }

        return new UserDetailsImpl(user.get());
    }
}
