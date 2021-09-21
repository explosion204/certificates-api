package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;

import java.util.List;
import java.util.Optional;

public interface GiftCertificateRepository {
    List<GiftCertificate> find(String tagName, String certificateName, String certificateDescription,
            OrderingType orderByName, OrderingType orderByCreateDate);
    Optional<GiftCertificate> findById(long id);
    boolean attachTag(long certificateId, long tagId);
    boolean detachTag(long certificateId, long tagId);

    long create(GiftCertificate certificate);
    boolean update(GiftCertificate certificate);
    boolean delete(long id);
}
