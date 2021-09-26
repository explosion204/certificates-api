package com.epam.esm.service;

import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.ValidationError;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private TagRepository tagRepository;
    private TagValidator tagValidator;

    public TagService(TagRepository tagRepository, TagValidator tagValidator) {
        this.tagRepository = tagRepository;
        this.tagValidator = tagValidator;
    }

    public List<TagDto> findAll() {
        return tagRepository.findAll()
                .stream()
                .map(TagDto::fromTag)
                .toList();
    }

    public TagDto findById(long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        return TagDto.fromTag(tag);
    }

    public long create(TagDto tagDto) {
        Tag tag = tagDto.toTag();
        List<ValidationError> validationErrors = tagValidator.validate(tag.getName());

        if (!validationErrors.isEmpty()) {
            throw new InvalidEntityException(validationErrors, Tag.class);
        }

        String name = tag.getName();
        if (tagRepository.findByName(name).isPresent()) {
            throw new EntityAlreadyExistsException();
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
