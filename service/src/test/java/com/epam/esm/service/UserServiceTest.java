package com.epam.esm.service;

import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.User;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.pagination.PageContext;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.security.KeycloakUtil;
import com.epam.esm.validator.UserValidator;
import com.epam.esm.validator.ValidationError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.entity.User.Role.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KeycloakUtil keycloakUtil;

    @BeforeAll
    static void setUp() {
        MockitoAnnotations.openMocks(UserServiceTest.class);
    }

    @Test
    void testFindAll() {
        PageContext pageContext = PageContext.of(null, null);
        PageRequest pageRequest = pageContext.toPageRequest();
        Page<User> resultPage = new PageImpl<>(provideUsers());
        when(userRepository.findAll(pageRequest)).thenReturn(resultPage);

        List<UserDto> expectedDtoList = provideUserDtoList();
        List<UserDto> actualDtoList = userService.findAll(pageContext).getContent();

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testFindById() {
        User user = provideUsers().get(0);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto expectedDto = provideUserDtoList().get(0);
        UserDto actualDto = userService.findById(user.getId());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testFindByIdWhenUserNotFound() {
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void testSignup() {
        UserDto userDto = provideUserDtoList().get(0);
        User user = provideUsers().get(0);

        when(userValidator.validate(user)).thenReturn(List.of());
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn(userDto.getPassword());
        when(userRepository.save(user)).thenReturn(user);
        when(keycloakUtil.keycloakUserExists(userDto.getUsername())).thenReturn(false);
        when(keycloakUtil.createKeycloakUser(user)).thenReturn("");

        userService.signup(userDto);

        verify(userValidator).validate(user);
        verify(userRepository).findByUsername(userDto.getUsername());
        verify(passwordEncoder).encode(userDto.getPassword());
        verify(userRepository).save(user);
        verify(keycloakUtil).keycloakUserExists(userDto.getUsername());
        verify(keycloakUtil).createKeycloakUser(user);
        verify(keycloakUtil).attachRoleToUser(user.getRole(), "");
        verify(keycloakUtil).obtainAccessToken(user.getUsername(), user.getPassword());
    }

    @Test
    void testSignupWhenUserIsInvalid() {
        UserDto userDto = provideUserDtoList().get(0);
        User user = provideUsers().get(0);

        when(userValidator.validate(user)).thenReturn(List.of(ValidationError.INVALID_USERNAME));

        assertThrows(InvalidEntityException.class, () -> userService.signup(userDto));
    }

    @Test
    void testSignupWhenUserAlreadyExists() {
        UserDto userDto = provideUserDtoList().get(0);
        User user = provideUsers().get(0);

        when(userValidator.validate(user)).thenReturn(List.of());
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user));

        assertThrows(EntityAlreadyExistsException.class, () -> userService.signup(userDto));
    }

    @Test
    void testLogin() {
        UserDto userDto = provideUserDtoList().get(0);
        User user = provideUsers().get(0);

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(user.getPassword(), userDto.getPassword())).thenReturn(true);
        when(keycloakUtil.keycloakUserExists(user.getUsername())).thenReturn(true);

        userService.login(userDto);

        verify(userRepository).findByUsername(userDto.getUsername());
        verify(passwordEncoder).matches(user.getPassword(), userDto.getPassword());
        verify(keycloakUtil).keycloakUserExists(user.getUsername());
        verify(keycloakUtil).resetPassword(user.getUsername(), user.getPassword());
        verify(keycloakUtil).obtainAccessToken(user.getUsername(), user.getPassword());
    }

    @Test
    void testLoginWhenUserDoesNotExist() {
        UserDto userDto = provideUserDtoList().get(0);

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> userService.login(userDto));
    }

    @Test
    void testLoginWhenCredentialsAreInvalid() {
        UserDto userDto = provideUserDtoList().get(0);
        User user = provideUsers().get(0);

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(user.getPassword(), userDto.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.login(userDto));
    }

    private List<User> provideUsers() {
        User firstUser = new User();
        firstUser.setId(1);
        firstUser.setUsername("user1");
        firstUser.setRole(USER);

        User secondUser = new User();
        secondUser.setId(2);
        secondUser.setUsername("user2");
        secondUser.setRole(USER);

        User thirdUser = new User();
        thirdUser.setId(3);
        thirdUser.setUsername("user3");
        thirdUser.setRole(USER);

        return List.of(firstUser, secondUser, thirdUser);
    }

    private List<UserDto> provideUserDtoList() {
        UserDto firstDto = new UserDto();
        firstDto.setId(1);
        firstDto.setUsername("user1");
        firstDto.setRole(USER.name());

        UserDto secondDto = new UserDto();
        secondDto.setId(2);
        secondDto.setUsername("user2");
        secondDto.setRole(USER.name());

        UserDto thirdDto = new UserDto();
        thirdDto.setId(3);
        thirdDto.setUsername("user3");
        thirdDto.setRole(USER.name());

        return List.of(firstDto, secondDto, thirdDto);
    }
}
