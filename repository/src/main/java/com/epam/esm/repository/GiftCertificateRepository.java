package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;

import java.util.List;
import java.util.Optional;

/**
 * Implementors of the interface provide functionality for manipulating stored {@link GiftCertificate} entities.
 *
 * @author Dmitry Karnyshov
 */
public interface GiftCertificateRepository {
    /**
     * Retrieve certificates according to specified parameters. All parameters are optional, so
     * if they are not present, all certificates will be retrieved
     *
     * @param tagName                precise tag name
     * @param certificateName        certificate name (can be partly qualified)
     * @param certificateDescription certificate description (can be partly qualified)
     * @param orderByName            name ordering approach
     * @param orderByCreateDate      creation date ordering approach
     * @return list of {@link GiftCertificate}
     */
    List<GiftCertificate> find(String tagName, String certificateName, String certificateDescription,
            OrderingType orderByName, OrderingType orderByCreateDate);

    /**
     * Retrieve certificate by its unique id.
     *
     * @param id certificate id
     * @return {@link GiftCertificate} wrapped by {@link Optional}
     */
    Optional<GiftCertificate> findById(long id);

    /**
     * Attach tag to existing certificate.
     *
     * @param certificateId certificate id
     * @param tagId         tag id
     */
    void attachTag(long certificateId, long tagId);

    /**
     * Detach tag from existing certificate.
     *
     * @param certificateId certificate id
     * @param tagId         tag id
     */
    void detachTag(long certificateId, long tagId);

    /**
     * Create a new certificate in the storage.
     *
     * @param certificate {@link GiftCertificate} instance
     * @return unique id of the saved {@link GiftCertificate}
     */
    long create(GiftCertificate certificate);

    /**
     * Update an existing certificate in the storage.
     *
     * @param certificate {@link GiftCertificate} instance
     * @return {@code true} if {@link GiftCertificate} existed and was updated, otherwise {@code false}
     */
    boolean update(GiftCertificate certificate);

    /**
     * Delete an existing certificate from the storage.
     *
     * @param id certificate id
     * @return {@code true} if {@link GiftCertificate} existed and was deleted, otherwise {@code false}
     */
    boolean delete(long id);
}
