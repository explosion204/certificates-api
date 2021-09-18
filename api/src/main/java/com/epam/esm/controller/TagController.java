package com.epam.esm.controller;

import com.epam.esm.controller.response.ResponseEntityFactory;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTag(@PathVariable("id") long id) throws ServiceException, EntityNotFoundException {
        Tag tag = tagService.findById(id);
        return ResponseEntityFactory.createResponseEntity(OK, tag);
    }

    @PostMapping
    public ResponseEntity<Object> createTag(@RequestBody Tag tag) throws EntityAlreadyExistsException, ServiceException,
                InvalidEntityException {
        long id = tagService.create(tag);
        return ResponseEntityFactory.createResponseEntity(CREATED, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable("id") long id) throws ServiceException,
                EntityNotFoundException {
        tagService.delete(id);
        return ResponseEntityFactory.createResponseEntity(OK);
    }
}
