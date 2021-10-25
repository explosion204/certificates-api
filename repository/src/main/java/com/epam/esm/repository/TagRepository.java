package com.epam.esm.repository;

import com.epam.esm.entity.Tag;

import java.util.List;
import java.util.Optional;

/**
 * Implementors of the interface provide functionality for manipulating stored {@link Tag} entities.
 *
 * @author Dmitry Karnyshov
 */
public interface TagRepository {
    /**
     * Retrieve all tags from storage.
     *
     * @param pageContext {@link PageContext} object with pagination logic
     * @return list of {@link Tag}
     */
    List<Tag> findAll(PageContext pageContext);

    /**
     * Retrieve tag by its unique id.
     *
     * @param id tag id
     * @return {@link Tag} wrapped by {@link Optional}
     */
    Optional<Tag> findById(long id);

    /**
     * Retrieve tag by its unique name.
     *
     * @param name tag name
     * @return {@link Tag} wrapped by {@link Optional}
     */
    Optional<Tag> findByName(String name);

    /**
     * Retrieve the most widely used tag of a user with the highest cost of all orders.
     *
     * @return {@link Tag} wrapped by {@link Optional}
     */
    Optional<Tag> findMostWidelyUsedTag();

    /**
     * Create a new tag in the storage.
     *
     * @param tag {@link Tag} instance
     * @return created {@link Tag}
     */
    Tag create(Tag tag);

    /**
     * Delete an existing tag from the storage.
     *
     * @param tag entity to delete
     */
    void delete(Tag tag);
}
