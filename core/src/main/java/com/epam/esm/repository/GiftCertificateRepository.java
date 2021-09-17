package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.RepositoryException;

import java.util.Optional;

public interface GiftCertificateRepository {
    Optional<GiftCertificate> findById(long id);

    long create(GiftCertificate certificate) throws RepositoryException;
    void update(GiftCertificate certificate) throws RepositoryException;
    void delete(long id) throws RepositoryException;
}
