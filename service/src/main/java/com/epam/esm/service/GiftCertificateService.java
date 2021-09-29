package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateSearchParamsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderingType;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validator.GiftCertificateValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.ValidationError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

/**
 * This service class encapsulated business logic related to {@link GiftCertificate} entity.
 *
 * @author Dmitry Karnyshov
 */
@Service
public class GiftCertificateService {
    private GiftCertificateRepository certificateRepository;
    private TagRepository tagRepository;
    private GiftCertificateValidator certificateValidator;
    private TagValidator tagValidator;

    public GiftCertificateService(
            GiftCertificateRepository certificateRepository,
            TagRepository tagRepository,
            GiftCertificateValidator certificateValidator,
            TagValidator tagValidator
    ) {
        this.certificateRepository = certificateRepository;
        this.tagRepository = tagRepository;
        this.certificateValidator = certificateValidator;
        this.tagValidator = tagValidator;
    }

    /**
     * Retrieve certificates according to specified parameters encapsulated in DTO object.
     * All parameters are optional, so if they are not present, all certificates will be retrieved.
     *
     * @param searchParamsDto {@link GiftCertificateSearchParamsDto} object with specified search parameters
     * @return list of {@link GiftCertificateDto}
     */
    public List<GiftCertificateDto> find(GiftCertificateSearchParamsDto searchParamsDto) {
        String tagName = searchParamsDto.getTagName();
        String certificateName = searchParamsDto.getCertificateName();
        String certificateDescription = searchParamsDto.getCertificateDescription();
        OrderingType orderByName = searchParamsDto.getOrderByName();
        OrderingType orderByCreateDate = searchParamsDto.getOrderByCreateDate();

        List<GiftCertificate> certificates = certificateRepository.find(tagName, certificateName, certificateDescription,
                orderByName, orderByCreateDate);

        return certificates.stream().map(certificate -> {
            long certificateId = certificate.getId();
            List<Tag> tags = tagRepository.findByCertificate(certificateId);
            return GiftCertificateDto.fromCertificate(certificate, tags);
        }).toList();
    }

    /**
     * Retrieve certificate by its unique id.
     *
     * @param id certificate id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @return {@link GiftCertificateDto} object
     */
    public GiftCertificateDto findById(long id) {
        GiftCertificate certificate = certificateRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(id));
        List<Tag> tags = tagRepository.findByCertificate(id);
        return GiftCertificateDto.fromCertificate(certificate, tags);
    }

    /**
     * Create a new certificate.
     *
     * @param certificateDto {@link GiftCertificateDto} instance
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @return {@link GiftCertificateDto} object that represents created {@link GiftCertificate}
     */
    @Transactional
    public GiftCertificateDto create(GiftCertificateDto certificateDto) {
        GiftCertificate certificate = certificateDto.toCertificate();
        List<String> tagNames = certificateDto.getTags();

        List<ValidationError> validationErrors = certificateValidator.validate(certificate, false);

        if (!validationErrors.isEmpty()) {
            throw new InvalidEntityException(validationErrors, GiftCertificate.class);
        }

        LocalDateTime createDate = LocalDateTime.now(UTC);
        certificate.setCreateDate(createDate);
        certificate.setLastUpdateDate(createDate);

        long certificateId = certificateRepository.create(certificate);
        certificateDto.setId(certificateId);

        // we do not update tags if it is not specified in request (i.e. tagNames == null)
        if (tagNames != null) {
            processTags(certificateId, tagNames);
            certificateDto.setTags(tagNames);
        }

        return certificateDto;
    }

    /**
     * Update an existing certificate.
     *
     * @param certificateDto {@link GiftCertificateDto} instance
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @return updated {@link GiftCertificateDto} object
     */
    @Transactional
    public GiftCertificateDto update(GiftCertificateDto certificateDto) {
        long certificateId = certificateDto.getId();
        Optional<GiftCertificate> optionalCertificate = certificateRepository.findById(certificateId);

        if (optionalCertificate.isEmpty()) {
            throw new EntityNotFoundException(certificateId);
        }

        GiftCertificate certificate = optionalCertificate.get();

        if (certificateDto.getName() != null) {
            certificate.setName(certificateDto.getName());
        }

        if (certificateDto.getDescription() != null) {
            certificate.setDescription(certificateDto.getDescription());
        }

        if (certificateDto.getDuration() != null) {
            certificate.setDuration(certificateDto.getDuration());
        }

        if (certificateDto.getPrice() != null) {
            certificate.setPrice(certificateDto.getPrice());
        }

        List<String> tagNames = certificateDto.getTags();
        List<ValidationError> validationErrors = certificateValidator.validate(certificate, true);

        if (!validationErrors.isEmpty()) {
            throw new InvalidEntityException(validationErrors, GiftCertificate.class);
        }

        LocalDateTime lastUpdateDate = LocalDateTime.now(UTC);
        certificate.setLastUpdateDate(lastUpdateDate);
        certificateRepository.update(certificate);

        // we do not update tags if it is not specified in request (i.e. tagNames == null)
        if (tagNames != null) {
            processTags(certificate.getId(), tagNames);
        }

        return findById(certificate.getId());
    }

    /**
     * Delete an existing certificate.
     *
     * @param id certificate id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     */
    public void delete(long id) {
        boolean certificateExists = certificateRepository.delete(id);

        if (!certificateExists) {
            throw new EntityNotFoundException(id);
        }
    }

    private void processTags(long certificateId, List<String> tagNames) {
        List<Tag> currentTags = tagRepository.findByCertificate(certificateId);
        // remove old tags
        currentTags.forEach(tag -> certificateRepository.detachTag(certificateId, tag.getId()));

        // tag validation
        tagNames.forEach(tagName -> {
            List<ValidationError> validationErrors = tagValidator.validate(tagName);

            if (!validationErrors.isEmpty()) {
                throw new InvalidEntityException(validationErrors, Tag.class);
            }

            // if tag does not exist, create it in database
            Optional<Tag> tag = tagRepository.findByName(tagName);
            long tagId;
            if (tag.isEmpty()) {
                Tag newTag = new Tag();
                newTag.setName(tagName);
                tagId = tagRepository.create(newTag);
            } else {
                tagId = tag.get().getId();
            }

            // attach tags to certificate
            certificateRepository.attachTag(certificateId, tagId);
        });
    }
}
