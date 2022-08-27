package com.epam.esm.controller;

import com.epam.esm.controller.hateoas.model.HateoasModel;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.controller.hateoas.model.PageHateoasModel;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.pagination.PageContext;
import com.epam.esm.exception.InvalidPageContextException;
import com.epam.esm.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.epam.esm.security.KeycloakAuthority.TAGS_DELETE;
import static com.epam.esm.security.KeycloakAuthority.TAGS_GET;
import static com.epam.esm.security.KeycloakAuthority.TAGS_SAVE;
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
    private HateoasProvider<TagDto> modelHateoasProvider;
    private HateoasProvider<List<TagDto>> listHateoasProvider;

    public TagController(TagService tagService, HateoasProvider<TagDto> modelHateoasProvider,
                HateoasProvider<List<TagDto>> listHateoasProvider) {
        this.tagService = tagService;
        this.modelHateoasProvider = modelHateoasProvider;
        this.listHateoasProvider = listHateoasProvider;
    }

    /**
     * Retrieve all tags.
     * Access is allowed to users with 'tags:get' authority (admin role).
     *
     * @throws InvalidPageContextException if passed page or page size values are invalid
     * @return JSON {@link ResponseEntity} object that contains list of {@link PageHateoasModel} objects
     */
    @GetMapping
    @PreAuthorize("hasAuthority('" + TAGS_GET + "')")
    public ResponseEntity<PageHateoasModel<TagDto>> getTags(@RequestParam(required = false) Integer page,
                                                            @RequestParam(required = false) Integer pageSize) {
        Page<TagDto> tags = tagService.findAll(PageContext.of(page, pageSize));
        PageHateoasModel<TagDto> model = PageHateoasModel.build(listHateoasProvider, tags);
        return new ResponseEntity<>(model, OK);
    }

    /**
     * Retrieve tag by its unique id.
     * Access is allowed to users with 'tags:get' authority (admin role).
     *
     * @param id tag id
     * @throws EntityNotFoundException in case when tag with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + TAGS_GET + "')")
    public ResponseEntity<HateoasModel<TagDto>> getTag(@PathVariable("id") long id) {
        TagDto tagDto = tagService.findById(id);
        HateoasModel<TagDto> model = HateoasModel.build(modelHateoasProvider, tagDto);
        return new ResponseEntity<>(model, OK);
    }

    /**
     * Retrieve the most widely used tag of a user with the highest cost of all orders.
     * Access is allowed to users with 'tags:get' authority (admin role).
     *
     * @throws EntityNotFoundException in case when such tag does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @GetMapping("/most_used_tag")
    @PreAuthorize("hasAuthority('" + TAGS_GET + "')")
    public ResponseEntity<HateoasModel<TagDto>> getMostWidelyTag() {
        TagDto tagDto = tagService.findMostWidelyUsedTag();
        HateoasModel<TagDto> model = HateoasModel.build(modelHateoasProvider, tagDto);
        return new ResponseEntity<>(model, OK);
    }

    /**
     * Create a new tag.
     * Access is allowed to users with 'tags:save' authority (admin role).
     *
     * @param tagDto {@link TagDto} instance
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @throws EntityAlreadyExistsException in case when tag with specified name already exists
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @PostMapping
    @PreAuthorize("hasAuthority('" + TAGS_SAVE + "')")
    public ResponseEntity<HateoasModel<TagDto>> createTag(@RequestBody TagDto tagDto) {
        TagDto createdTagDto = tagService.create(tagDto);
        HateoasModel<TagDto> model = HateoasModel.build(modelHateoasProvider, createdTagDto);
        return new ResponseEntity<>(model, CREATED);
    }

    /**
     * Delete an existing tag.
     * Access is allowed to users with 'tags:delete' authority (admin role).
     *
     * @param id tag id
     * @throws EntityNotFoundException in case when tag with this id does not exist
     * @return empty {@link ResponseEntity}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + TAGS_DELETE + "')")
    public ResponseEntity<Void> deleteTag(@PathVariable("id") long id) {
        tagService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
