package com.epam.esm.controller.hateoas;

import com.epam.esm.controller.TagController;
import com.epam.esm.dto.TagDto;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Aspect
@Component
class TagControllerHateoasAspect extends BaseHateoasAspect<TagDto> {
    private static final String MOST_USED_TAG_REL = "most used tag";
    private static final String MOST_USED_TAG = "most_used_tag";

    TagControllerHateoasAspect() {
        super(TagController.class);
    }

    @AfterReturning(pointcut = SINGLE_ENTITY_POINTCUT_PATTERN, returning = RETURN_VALUE_NAME)
    ResponseEntity<TagDto> singleEntityAdvice(ResponseEntity<TagDto> responseEntity) {
        return applyForSingleEntity(responseEntity);
    }

    @AfterReturning(pointcut = LIST_POINTCUT_PATTERN, returning = RETURN_VALUE_NAME)
    ResponseEntity<List<TagDto>> listAdvice(ResponseEntity<List<TagDto>> responseEntity) {
        return applyForList(responseEntity);
    }

    @Override
    void processModel(TagDto model) {
        Link selfLink = LinkConstructor.constructSelfLink(controllerClass, model);
        Link listLink = LinkConstructor.constructListLink(controllerClass);
        Link createLink = LinkConstructor.constructCreateLink(controllerClass);
        Link deleteLink = LinkConstructor.constructDeleteLink(controllerClass, model);
        Link mostUsedTagLink = constructWidelyUsedTagLink();

        model.addLinks(selfLink, listLink, createLink, deleteLink, mostUsedTagLink);
    }

    private Link constructWidelyUsedTagLink() {
        return linkTo(controllerClass).slash(MOST_USED_TAG)
                .withRel(MOST_USED_TAG_REL);
    }
}
