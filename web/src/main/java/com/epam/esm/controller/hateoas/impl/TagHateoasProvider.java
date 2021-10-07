package com.epam.esm.controller.hateoas.impl;

import com.epam.esm.controller.TagController;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.controller.hateoas.LinkConstructor;
import com.epam.esm.dto.TagDto;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class TagHateoasProvider implements HateoasProvider<TagDto> {
    private static final String MOST_WIDELY_USED_TAG_REL = "mostWidelyUsedTag";
    private static final String MOST_WIDELY_USED_TAG_URI = "most_used_tag";

    @Override
    public List<Link> provide(TagDto model) {
        Class<?> controllerClass = TagController.class;

        Link selfLink = LinkConstructor.constructSelfLink(controllerClass, model);
        Link allResourcesLink = LinkConstructor.constructControllerLink(controllerClass);
        Link mostWidelyUsedTagLink = constructWidelyUsedTagLink(controllerClass);

        return List.of(selfLink, allResourcesLink, mostWidelyUsedTagLink);
    }

    private Link constructWidelyUsedTagLink(Class<?> controllerClass) {
        return linkTo(controllerClass).slash(MOST_WIDELY_USED_TAG_REL)
                .withRel(MOST_WIDELY_USED_TAG_URI);
    }
}
