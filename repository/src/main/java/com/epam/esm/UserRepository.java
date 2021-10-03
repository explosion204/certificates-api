package com.epam.esm;

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
     * @return list of {@link User}
     */
    List<User> findAll();

    /**
     * Retrieve user by its unique id.
     *
     * @param id user id
     * @return {@link User} wrapped by {@link Optional}
     */
    Optional<User> findById(long id);
}
