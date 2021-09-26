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

/**
 * This service class encapsulated business logic related to {@link Tag} entity.
 *
 * @author Dmitry Karnyshov
 */
@Service
public class TagService {
    private TagRepository tagRepository;
    private TagValidator tagValidator;

    public TagService(TagRepository tagRepository, TagValidator tagValidator) {
        this.tagRepository = tagRepository;
        this.tagValidator = tagValidator;
    }

    /**
     * Retrieve all tags.
     *
     * @return list of {@link TagDto}
     */
    public List<TagDto> findAll() {
        return tagRepository.findAll()
                .stream()
                .map(TagDto::fromTag)
                .toList();
    }

    /**
     * Retrieve tag by its unique id.
     *
     * @param id tag id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @return {@link TagDto} object
     */
    public TagDto findById(long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        return TagDto.fromTag(tag);
    }

    /**
     * Create a new tag.
     *
     * @param tagDto {@link TagDto} instance
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @throws EntityAlreadyExistsException in case when tag with specified name already exists
     * @return unique id of the saved {@link Tag}
     */
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

    /**
     * Delete an existing tag.
     *
     * @param id tag id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     */
    public void delete(long id) {
        boolean tagExists = tagRepository.delete(id);

        if (!tagExists) {
            throw new EntityNotFoundException(id);
        }
    }
}
