package com.epam.esm.service;

import com.epam.esm.dto.TokenDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.ApplicationAuthenticationException;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.entity.User;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.security.KeycloakUtil;
import com.epam.esm.validator.UserValidator;
import com.epam.esm.validator.ValidationError;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.epam.esm.exception.ApplicationAuthenticationException.ErrorType.INVALID_CREDENTIALS;

/**
 * This service class encapsulated business logic related to {@link User} entity.
 *
 * @author Dmitry Karnyshov
 */
@Service
public class UserService {
    private UserRepository userRepository;
    private UserValidator userValidator;
    private KeycloakUtil keycloakUtil;
    private PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserValidator userValidator,
            KeycloakUtil keycloakUtil,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.keycloakUtil = keycloakUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieve all users.
     *
     * @param pageContext {@link PageContext} object with pagination logic
     * @return list of {@link UserDto}
     */
    public List<UserDto> findAll(PageContext pageContext) {
        return userRepository.findAll(pageContext)
                .stream()
                .map(UserDto::fromUser)
                .toList();
    }

    /**
     * Retrieve user by its unique id.
     *
     * @param id user id
     * @throws EntityNotFoundException in case when user with this id does not exist
     * @return {@link UserDto} object
     */
    public UserDto findById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, User.class));
        return UserDto.fromUser(user);
    }

    @Transactional
    public TokenDto signup(UserDto userDto) {
        User user = userDto.toUser();
        List<ValidationError> validationErrors = userValidator.validate(user);

        if (!validationErrors.isEmpty()) {
            throw new InvalidEntityException(validationErrors, User.class);
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new EntityAlreadyExistsException();
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        User createdUser = userRepository.create(user);
        return buildTokenDto(createdUser);
    }

    @Transactional
    public TokenDto login(UserDto userDto) {
        String username = userDto.getUsername();
        String password = userDto.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationAuthenticationException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApplicationAuthenticationException(INVALID_CREDENTIALS);
        }

        return buildTokenDto(user);
    }

    private TokenDto buildTokenDto(User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        if (!keycloakUtil.keycloakUserExists(username)) {
            String keycloakUserId = keycloakUtil.createKeycloakUser(user);
            keycloakUtil.attachRoleToUser(user.getRole(), keycloakUserId);
        } else {
            keycloakUtil.resetPassword(username, password);
        }

        String accessToken = keycloakUtil.obtainAccessToken(username, password);

        TokenDto tokenDto = new TokenDto();
        tokenDto.setAccessToken(accessToken);

        return tokenDto;
    }
}
