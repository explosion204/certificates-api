package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.RepositoryException;

import java.util.Optional;

public interface GiftCertificateRepository {
    Optional<GiftCertificate> findById(long id) throws RepositoryException;
    boolean attachTag(long certificateId, long tagId) throws RepositoryException;
    boolean detachTag(long certificateId, long tagId) throws RepositoryException;

    long create(GiftCertificate certificate) throws RepositoryException;
    boolean update(GiftCertificate certificate) throws RepositoryException;
    boolean delete(long id) throws RepositoryException;
}
