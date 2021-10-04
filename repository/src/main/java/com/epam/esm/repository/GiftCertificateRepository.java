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
     * @param tagNames               precise tag names
     * @param certificateName        certificate name (can be partly qualified)
     * @param certificateDescription certificate description (can be partly qualified)
     * @param orderByName            name ordering approach
     * @param orderByCreateDate      creation date ordering approach
     * @return list of {@link GiftCertificate}
     */
    List<GiftCertificate> find(List<String> tagNames, String certificateName, String certificateDescription,
            OrderingType orderByName, OrderingType orderByCreateDate);

    /**
     * Retrieve certificate by its unique id.
     *
     * @param id certificate id
     * @return {@link GiftCertificate} wrapped by {@link Optional}
     */
    Optional<GiftCertificate> findById(long id);

    /**
     * Create a new certificate in the storage.
     *
     * @param certificate {@link GiftCertificate} instance
     * @return created {@link GiftCertificate}
     */
    GiftCertificate create(GiftCertificate certificate);

    /**
     * Update an existing certificate in the storage.
     *
     * @param certificate {@link GiftCertificate} instance
     * @return updated {@link GiftCertificate}
     */
    GiftCertificate update(GiftCertificate certificate);

    /**
     * Delete an existing certificate from the storage.
     *
     * @param certificate entity to delete
     */
    void delete(GiftCertificate certificate);
}
