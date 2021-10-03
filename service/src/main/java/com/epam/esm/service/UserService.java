package com.epam.esm.service;

import com.epam.esm.dto.UserDto;
import com.epam.esm.UserRepository;
import com.epam.esm.entity.User;
import com.epam.esm.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service class encapsulated business logic related to {@link User} entity.
 *
 * @author Dmitry Karnyshov
 */
@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieve all users.
     *
     * @return list of {@link UserDto}
     */
    public List<UserDto> findAll() {
        return userRepository.findAll()
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
    public UserDto find(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        return UserDto.fromUser(user);
    }
}
