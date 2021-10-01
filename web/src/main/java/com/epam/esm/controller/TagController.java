package com.epam.esm.controller;

import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

/**
 * This class contains public REST API endpoints related to {@link Tag} entity.
 *
 * @author Dmitry Karnyshov
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {
    private TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Retrieve all tags.
     *
     * @return JSON {@link ResponseEntity} object that contains list of {@link TagDto}
     */
    @GetMapping
    public ResponseEntity<List<TagDto>> getTags() {
        List<TagDto> tags = tagService.findAll();
        return new ResponseEntity<>(tags, OK);
    }

    /**
     * Retrieve tag by its unique id.
     *
     * @param id tag id
     * @throws EntityNotFoundException in case when tag with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link TagDto} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTag(@PathVariable("id") long id) {
        TagDto tagDto = tagService.findById(id);
        return new ResponseEntity<>(tagDto, OK);
    }

    /**
     * Create a new tag.
     *
     * @param tagDto {@link TagDto} instance
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @throws EntityAlreadyExistsException in case when tag with specified name already exists
     * @return JSON {@link ResponseEntity} object that contains created {@link TagDto} object
     */
    @PostMapping
    public ResponseEntity<TagDto> createTag(@RequestBody TagDto tagDto) {
        TagDto createdTagDto = tagService.create(tagDto);
        return new ResponseEntity<>(createdTagDto, CREATED);
    }

    /**
     * Delete an existing tag.
     *
     * @param id tag id
     * @throws EntityNotFoundException in case when tag with this id does not exist
     * @return empty {@link ResponseEntity}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable("id") long id) {
        tagService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
