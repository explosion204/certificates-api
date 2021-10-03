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
     * @return list of {@link Tag}
     */
    List<Tag> findAll();

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
