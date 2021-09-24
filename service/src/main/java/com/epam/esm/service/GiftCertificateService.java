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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

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

    public GiftCertificateDto findById(long id) {
        GiftCertificate certificate = certificateRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(id));
        List<Tag> tags = tagRepository.findByCertificate(id);
        return GiftCertificateDto.fromCertificate(certificate, tags);
    }

    @Transactional
    public long create(GiftCertificateDto certificateDto) {
        GiftCertificate certificate = certificateDto.toCertificate();
        List<String> tagNames = certificateDto.getTags();

        List<ValidationError> validationErrors = certificateValidator.validate(certificate, false);

        if (!validationErrors.isEmpty()) {
            throw new InvalidEntityException(validationErrors, GiftCertificate.class);
        }

        ZonedDateTime createDate = Instant.now().atZone(UTC);
        certificate.setCreateDate(createDate);
        certificate.setLastUpdateDate(createDate);

        long certificateId = certificateRepository.create(certificate);

        // we do not update tags if it is not specified in request (i.e. tagNames == null)
        if (tagNames != null) {
            processTags(certificateId, tagNames);
        }

        return certificateId;
    }

    @Transactional
    public GiftCertificateDto update(GiftCertificateDto certificateDto) {
        GiftCertificate certificate = certificateDto.toCertificate();
        List<String> tagNames = certificateDto.getTags();

        List<ValidationError> validationErrors = certificateValidator.validate(certificate, true);

        if (!validationErrors.isEmpty()) {
            throw new InvalidEntityException(validationErrors, GiftCertificate.class);
        }

        ZonedDateTime lastUpdateDate = Instant.now().atZone(UTC);
        certificate.setLastUpdateDate(lastUpdateDate);

        boolean certificateExists = certificateRepository.update(certificate);

        if (!certificateExists) {
            throw new EntityNotFoundException(certificate.getId());
        }

        // we do not update tags if it is not specified in request (i.e. tagNames == null)
        if (tagNames != null) {
            processTags(certificate.getId(), tagNames);
        }

        return findById(certificate.getId());
    }

    public void delete(long id) {
        boolean certificateExists = certificateRepository.delete(id);

        if (!certificateExists) {
            throw new EntityNotFoundException(id);
        }
    }

    private void processTags(long certificateId, List<String> tagNames) {
        List<Tag> currentTags = tagRepository.findByCertificate(certificateId);

        // remove old tags
        for (Tag tag : currentTags) {
            certificateRepository.detachTag(certificateId, tag.getId());
        }

        // tag validation
        for (String tagName : tagNames) {
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
        }
    }
}
