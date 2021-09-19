package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.RepositoryException;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validator.GiftCertificateValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.ValidationError;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

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

    public GiftCertificateDto findById(long id) throws EntityNotFoundException, ServiceException {
        try {
            GiftCertificate certificate = certificateRepository.findById(id).orElseThrow(() ->
                    new EntityNotFoundException(id));
            List<Tag> tags = tagRepository.findByCertificate(id);
            return GiftCertificateDto.fromCertificate(certificate, tags);
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    public long create(GiftCertificateDto certificateDto) throws InvalidEntityException, ServiceException {
        GiftCertificate certificate = certificateDto.toCertificate();
        List<String> tagNames = certificateDto.getTags();

        Pair<Boolean, EnumSet<ValidationError>> certificateValidationResult
                = certificateValidator.validate(certificate, false);
        boolean certificateValidationStatus = certificateValidationResult.getLeft();
        EnumSet<ValidationError> certificateValidationErrors = certificateValidationResult.getRight();

        if (!certificateValidationStatus) {
            throw new InvalidEntityException(certificateValidationErrors, GiftCertificate.class);
        }

        ZonedDateTime createDate = Instant.now().atZone(UTC);
        certificateDto.setCreateDate(createDate);
        certificateDto.setLastUpdateDate(createDate);

        try {
            long certificateId = certificateRepository.create(certificate);
            processTags(certificateId, tagNames);
            return certificateId;
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    public GiftCertificateDto update(GiftCertificateDto certificateDto) throws InvalidEntityException,
                EntityNotFoundException, ServiceException {
        GiftCertificate certificate = certificateDto.toCertificate();
        List<String> tagNames = certificateDto.getTags();

        Pair<Boolean, EnumSet<ValidationError>> certificateValidationResult
                = certificateValidator.validate(certificate, true);
        boolean certificateValidationStatus = certificateValidationResult.getLeft();
        EnumSet<ValidationError> certificateValidationErrors = certificateValidationResult.getRight();

        if (!certificateValidationStatus) {
            throw new InvalidEntityException(certificateValidationErrors, GiftCertificate.class);
        }

        ZonedDateTime lastUpdateDate = Instant.now().atZone(UTC);
        certificate.setLastUpdateDate(lastUpdateDate);

        try {
            boolean certificateExists = certificateRepository.update(certificate);

            if (!certificateExists) {
                throw new EntityNotFoundException(certificate.getId());
            }

            processTags(certificate.getId(), tagNames);

            return findById(certificate.getId());
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    public void delete(long id) throws EntityNotFoundException, ServiceException {
        try {
            boolean certificateExists = certificateRepository.delete(id);

            if (!certificateExists) {
                throw new EntityNotFoundException(id);
            }
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    private void processTags(long certificateId, List<String> tagNames) throws InvalidEntityException,
                RepositoryException {
        List<Tag> currentTags = tagRepository.findByCertificate(certificateId);

        // remove old tags
        for (Tag tag : currentTags) {
            certificateRepository.detachTag(certificateId, tag.getId());
        }

        // tag validation
        for (String tagName : tagNames) {
            Pair<Boolean, EnumSet<ValidationError>> tagValidationResult = tagValidator.validate(tagName);
            boolean tagValidationStatus = tagValidationResult.getLeft();
            EnumSet<ValidationError> tagValidationErrors = tagValidationResult.getRight();

            if (!tagValidationStatus) {
                throw new InvalidEntityException(tagValidationErrors, Tag.class);
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
