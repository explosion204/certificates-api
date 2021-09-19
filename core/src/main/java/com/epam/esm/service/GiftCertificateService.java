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
            return GiftCertificateDto.fromCertificate(certificate);
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    public long create(GiftCertificateDto certificateDto) throws InvalidEntityException, ServiceException {
        GiftCertificate certificate = certificateDto.toCertificate();
        List<Tag> tags = certificateDto.getTags();

        Pair<Boolean, EnumSet<ValidationError>> certificateValidationResult
                = certificateValidator.validate(certificate, false);
        boolean certificateValidationStatus = certificateValidationResult.getLeft();
        EnumSet<ValidationError> certificateValidationErrors = certificateValidationResult.getRight();

        if (!certificateValidationStatus) {
            throw new InvalidEntityException(certificateValidationErrors, GiftCertificate.class);
        }

        for (Tag tag : tags) {
            Pair<Boolean, EnumSet<ValidationError>> tagValidationResult = tagValidator.validate(tag);
            boolean tagValidationStatus = tagValidationResult.getLeft();
            EnumSet<ValidationError> tagValidationErrors = tagValidationResult.getRight();

            if (!tagValidationStatus) {
                throw new InvalidEntityException(tagValidationErrors, Tag.class);
            }
        }

        ZonedDateTime createDate = Instant.now().atZone(UTC);
        certificateDto.setCreateDate(createDate);
        certificateDto.setLastUpdateDate(createDate);
        try {
            return certificateRepository.create(certificate);
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    public void update(GiftCertificateDto certificateDto) throws InvalidEntityException,
                EntityNotFoundException, ServiceException {
        GiftCertificate certificate = certificateDto.toCertificate();
        List<Tag> tags = certificateDto.getTags();

        Pair<Boolean, EnumSet<ValidationError>> certificateValidationResult
                = certificateValidator.validate(certificate, true);
        boolean certificateValidationStatus = certificateValidationResult.getLeft();
        EnumSet<ValidationError> certificateValidationErrors = certificateValidationResult.getRight();

        if (!certificateValidationStatus) {
            throw new InvalidEntityException(certificateValidationErrors, GiftCertificate.class);
        }

        // TODO: 9/18/2021 add tags

        ZonedDateTime lastUpdateDate = Instant.now().atZone(UTC);
        certificate.setLastUpdateDate(lastUpdateDate);

        try {
            boolean certificateExists = certificateRepository.update(certificate);

            if (!certificateExists) {
                throw new EntityNotFoundException(certificate.getId());
            }
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
}
