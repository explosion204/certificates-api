package com.epam.esm.service;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.*;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.ValidationError;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class TagService {
    private TagRepository tagRepository;
    private TagValidator tagValidator;

    public TagService(TagRepository tagRepository, TagValidator tagValidator) {
        this.tagRepository = tagRepository;
        this.tagValidator = tagValidator;
    }

    public Tag findById(long id) throws EntityNotFoundException, ServiceException {
        try {
            return tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    public long create(Tag tag) throws InvalidEntityException, EntityAlreadyExistsException, ServiceException {
        Pair<Boolean, EnumSet<ValidationError>> tagValidationResult = tagValidator.validate(tag);
        boolean tagValidationStatus = tagValidationResult.getLeft();
        EnumSet<ValidationError> tagValidationErrors = tagValidationResult.getRight();

        if (!tagValidationStatus) {
            throw new InvalidEntityException(tagValidationErrors);
        }

        try {
            String name = tag.getName();
            if (tagRepository.findByName(name).isPresent()) {
                throw new EntityAlreadyExistsException(tag);
            }

            return tagRepository.create(tag);
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }

    public void delete(long id) throws EntityNotFoundException, ServiceException {
        try {
            boolean tagExists = tagRepository.delete(id);

            if (!tagExists) {
                throw new EntityNotFoundException(id);
            }
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }
}
