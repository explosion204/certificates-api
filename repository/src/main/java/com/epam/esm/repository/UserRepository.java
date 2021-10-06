package com.epam.esm.repository;

import com.epam.esm.entity.User;
import com.epam.esm.repository.exception.InvalidPageContextException;

import java.util.List;
import java.util.Optional;

/**
 * Implementors of the interface provide functionality for manipulating stored {@link User} entities.
 *
 * @author Dmitry Karnyshov
 */
public interface UserRepository {
    /**
     * Retrieve all users from storage.
     *
     * @param pageContext {@link PageContext} object with pagination logic
     * @throws InvalidPageContextException if passed page or page size values are invalid
     * @return list of {@link User}
     */
    List<User> findAll(PageContext pageContext);

    /**
     * Retrieve user by its unique id.
     *
     * @param id user id
     * @return {@link User} wrapped by {@link Optional}
     */
    Optional<User> findById(long id);
}
