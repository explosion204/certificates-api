package com.epam.esm.repository;

import com.epam.esm.entity.User;

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

    // TODO: 10/22/2021 DOCS
    Optional<User> findByUsername(String username);

    User create(User user);
}
