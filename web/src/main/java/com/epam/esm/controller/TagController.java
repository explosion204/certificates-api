package com.epam.esm.controller;

import com.epam.esm.controller.hateoas.HateoasModel;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.PageContext;
import com.epam.esm.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
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
    private HateoasProvider<TagDto> hateoasProvider;

    public TagController(TagService tagService, HateoasProvider<TagDto> hateoasProvider) {
        this.tagService = tagService;
        this.hateoasProvider = hateoasProvider;
    }

    /**
     * Retrieve all tags.
     *
     * @return JSON {@link ResponseEntity} object that contains list of {@link HateoasModel} objects
     */
    @GetMapping
    public ResponseEntity<List<HateoasModel>> getTags(@ModelAttribute PageContext pageContext) {
        List<TagDto> tags = tagService.findAll(pageContext);
        List<HateoasModel> models = HateoasModel.build(hateoasProvider, tags);
        return new ResponseEntity<>(models, OK);
    }

    /**
     * Retrieve tag by its unique id.
     *
     * @param id tag id
     * @throws EntityNotFoundException in case when tag with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<HateoasModel> getTag(@PathVariable("id") long id) {
        TagDto tagDto = tagService.findById(id);
        HateoasModel model = HateoasModel.build(hateoasProvider, tagDto);
        return new ResponseEntity<>(model, OK);
    }

    /**
     * Retrieve the most widely used tag of a user with the highest cost of all orders.
     *
     * @throws EntityNotFoundException in case when such tag does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @GetMapping("/most_used_tag")
    public ResponseEntity<HateoasModel> getMostWidelyTag() {
        TagDto tagDto = tagService.findMostWidelyUsedTag();
        HateoasModel model = HateoasModel.build(hateoasProvider, tagDto);
        return new ResponseEntity<>(model, OK);
    }

    /**
     * Create a new tag.
     *
     * @param tagDto {@link TagDto} instance
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @throws EntityAlreadyExistsException in case when tag with specified name already exists
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @PostMapping
    public ResponseEntity<HateoasModel> createTag(@RequestBody TagDto tagDto) {
        TagDto createdTagDto = tagService.create(tagDto);
        HateoasModel model = HateoasModel.build(hateoasProvider, createdTagDto);
        return new ResponseEntity<>(model, CREATED);
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
