package com.epam.esm.service;

import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
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

    public TagDto findById(long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        return TagDto.fromTag(tag);
    }

    public long create(TagDto tagDto) {
        Tag tag = tagDto.toTag();
        Pair<Boolean, EnumSet<ValidationError>> tagValidationResult = tagValidator.validate(tag.getName());
        boolean tagValidationStatus = tagValidationResult.getLeft();
        EnumSet<ValidationError> tagValidationErrors = tagValidationResult.getRight();

        if (!tagValidationStatus) {
            throw new InvalidEntityException(tagValidationErrors, Tag.class);
        }

        String name = tag.getName();
        if (tagRepository.findByName(name).isPresent()) {
            throw new EntityAlreadyExistsException(tag);
        }

        return tagRepository.create(tag);
    }

    public void delete(long id) {
        boolean tagExists = tagRepository.delete(id);

        if (!tagExists) {
            throw new EntityNotFoundException(id);
        }
    }
}
