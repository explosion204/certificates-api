package com.epam.esm.service;

import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.User;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeAll
    static void setUp() {
        MockitoAnnotations.openMocks(UserServiceTest.class);
    }

    @Test
    void testFindAll() {
        PageContext pageContext = new PageContext();
        when(userRepository.findAll(pageContext)).thenReturn(provideUsers());

        List<UserDto> expectedDtoList = provideUserDtoList();
        List<UserDto> actualDtoList = userService.findAll(pageContext);

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

    private List<User> provideUsers() {
        User firstUser = new User();
        firstUser.setId(1);
        firstUser.setName("user1");

        User secondUser = new User();
        secondUser.setId(2);
        secondUser.setName("user2");

        User thirdUser = new User();
        thirdUser.setId(3);
        thirdUser.setName("user3");

        return List.of(firstUser, secondUser, thirdUser);
    }

    private List<UserDto> provideUserDtoList() {
        UserDto firstDto = new UserDto();
        firstDto.setId(1);
        firstDto.setName("user1");

        UserDto secondDto = new UserDto();
        secondDto.setId(2);
        secondDto.setName("user2");

        UserDto thirdDto = new UserDto();
        thirdDto.setId(3);
        thirdDto.setName("user3");

        return List.of(firstDto, secondDto, thirdDto);
    }
}
