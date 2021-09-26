package com.epam.esm.controller;

import com.epam.esm.controller.response.ResponseEntityFactory;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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
     * @return JSON {@link ResponseEntity} object that contains {@link HttpStatus} code
     * and list of {@link TagDto}
     */
    @GetMapping
    public ResponseEntity<Object> getTags() {
        List<TagDto> tags = tagService.findAll();
        return ResponseEntityFactory.createResponseEntity(OK, tags);
    }

    /**
     * Retrieve tag by its unique id.
     *
     * @param id tag id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HttpStatus} code
     * and {@link TagDto} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getTag(@PathVariable("id") long id) {
        TagDto tagDto = tagService.findById(id);
        return ResponseEntityFactory.createResponseEntity(OK, tagDto);
    }

    /**
     * Create a new tag.
     *
     * @param tagDto {@link TagDto} instance
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @throws EntityAlreadyExistsException in case when tag with specified name already exists
     * @return JSON {@link ResponseEntity} object that contains {@link HttpStatus} code
     * and unique id of the created {@link Tag}
     */
    @PostMapping
    public ResponseEntity<Object> createTag(@RequestBody TagDto tagDto) {
        long id = tagService.create(tagDto);
        return ResponseEntityFactory.createResponseEntity(CREATED, id);
    }

    /**
     * Delete an existing tag.
     *
     * @param id tag id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HttpStatus} code
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTag(@PathVariable("id") long id) {
        tagService.delete(id);
        return ResponseEntityFactory.createResponseEntity(OK);
    }
}
