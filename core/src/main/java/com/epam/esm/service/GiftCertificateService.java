package com.epam.esm.service;

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
import java.util.Set;

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

    public GiftCertificate findById(long id) throws EntityNotFoundException, ServiceException {
        try {
            return certificateRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    public long create(GiftCertificate certificate, Set<Tag> tags) throws InvalidEntityException, ServiceException {
        Pair<Boolean, EnumSet<ValidationError>> certificateValidationResult
                = certificateValidator.validate(certificate, false);
        boolean certificateValidationStatus = certificateValidationResult.getLeft();
        EnumSet<ValidationError> certificateValidationErrors = certificateValidationResult.getRight();

        if (!certificateValidationStatus) {
            throw new InvalidEntityException(certificateValidationErrors);
        }

        // TODO: 9/18/2021 add tags
//        for (Tag tag : tags) {
//            Pair<Boolean, EnumSet<ValidationError>> tagValidationResult = tagValidator.validate(tag);
//            boolean tagValidationStatus = tagValidationResult.getLeft();
//            EnumSet<ValidationError> tagValidationErrors = tagValidationResult.getRight();
//
//            if (!tagValidationStatus) {
//                throw new InvalidEntityException(tagValidationErrors, tag);
//            }
//        }

        ZonedDateTime createDate = Instant.now().atZone(UTC);
        certificate.setCreateDate(createDate);
        certificate.setLastUpdateDate(createDate);
        try {
            return certificateRepository.create(certificate);
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    public void update(GiftCertificate certificate, Set<Tag> tags) throws InvalidEntityException,
            EntityNotFoundException, ServiceException {
        Pair<Boolean, EnumSet<ValidationError>> certificateValidationResult
                = certificateValidator.validate(certificate, true);
        boolean certificateValidationStatus = certificateValidationResult.getLeft();
        EnumSet<ValidationError> certificateValidationErrors = certificateValidationResult.getRight();

        if (!certificateValidationStatus) {
            throw new InvalidEntityException(certificateValidationErrors, certificate);
        }

        // TODO: 9/18/2021 add tags

        ZonedDateTime createDate = Instant.now().atZone(UTC);
        certificate.setCreateDate(createDate);
        certificate.setLastUpdateDate(createDate);

        try {
            boolean certificateExists = certificateRepository.update(certificate);

            if (!certificateExists) {
                throw new EntityNotFoundException();
            }
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    public void delete(long id) throws EntityNotFoundException, ServiceException {
        try {
            boolean certificateExists = certificateRepository.delete(id);

            if (!certificateExists) {
                throw new EntityNotFoundException();
            }
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }
}
