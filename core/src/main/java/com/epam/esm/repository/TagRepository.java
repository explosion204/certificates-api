package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.RepositoryException;

import java.util.List;
import java.util.Optional;

public interface TagRepository {
    Optional<Tag> findById(long id) throws RepositoryException;
    Optional<Tag> findByName(String name) throws RepositoryException;
    List<Tag> findByCertificate(long certificateId) throws RepositoryException;

    long create(Tag tag) throws RepositoryException;
    boolean delete(long id) throws RepositoryException;
}
