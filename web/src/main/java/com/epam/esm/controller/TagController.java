package com.epam.esm.controller;

import com.epam.esm.controller.response.ResponseEntityFactory;
import com.epam.esm.dto.TagDto;
import com.epam.esm.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<Object> getTags() {
        List<TagDto> tags = tagService.findAll();
        return ResponseEntityFactory.createResponseEntity(OK, tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTag(@PathVariable("id") long id) {
        TagDto tagDto = tagService.findById(id);
        return ResponseEntityFactory.createResponseEntity(OK, tagDto);
    }

    @PostMapping
    public ResponseEntity<Object> createTag(@RequestBody TagDto tagDto) {
        long id = tagService.create(tagDto);
        return ResponseEntityFactory.createResponseEntity(CREATED, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTag(@PathVariable("id") long id) {
        tagService.delete(id);
        return ResponseEntityFactory.createResponseEntity(OK);
    }
}
