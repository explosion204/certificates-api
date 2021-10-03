package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
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
     * Retrieve all tags attached to a certain certificate.
     *
     * @param certificateId certificate id
     * @return list of {@link Tag} attached to the {@link GiftCertificate}
     */
    List<Tag> findByCertificate(long certificateId);

    /**
     * Create a new tag in the storage.
     *
     * @param tag {@link Tag} instance
     * @return unique id of the saved {@link Tag}
     */
    long create(Tag tag);

    /**
     * Delete an existing tag from the storage.
     *
     * @param id tag id
     * @return {@code true} if {@link Tag} existed and was deleted, otherwise {@code false}
     */
    boolean delete(long id);
}
