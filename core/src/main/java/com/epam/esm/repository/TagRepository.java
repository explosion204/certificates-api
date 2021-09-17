package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.RepositoryException;

import java.util.Optional;

public interface TagRepository {
    Optional<Tag> findById(long id) throws RepositoryException;

    long create(Tag tag) throws RepositoryException;
    void delete(long id) throws RepositoryException;
}
