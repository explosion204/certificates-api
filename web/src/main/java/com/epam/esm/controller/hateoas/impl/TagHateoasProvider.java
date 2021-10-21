package com.epam.esm.controller.hateoas.impl;

import com.epam.esm.controller.TagController;
import com.epam.esm.controller.hateoas.ModelHateoasProvider;
import com.epam.esm.dto.TagDto;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.epam.esm.controller.hateoas.impl.ResourceRelName.ALL_TAGS_REL;
import static com.epam.esm.controller.hateoas.impl.ResourceRelName.MOST_WIDELY_USED_TAG_REL;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class TagHateoasProvider extends ModelHateoasProvider<TagDto> {
    private static final String MOST_WIDELY_USED_TAG_URI = "most_used_tag";

    @Override
    protected List<Link> addSpecificLinks(List<Link> baseLinks, TagDto model) {
        Link mostWidelyUsedTagLink = constructWidelyUsedTagLink(getControllerClass());
        baseLinks.add(mostWidelyUsedTagLink);

        return baseLinks;
    }

    @Override
    protected Class<?> getControllerClass() {
        return TagController.class;
    }

    @Override
    protected String getAllResourcesRel() {
        return ALL_TAGS_REL;
    }

    private Link constructWidelyUsedTagLink(Class<?> controllerClass) {
        return linkTo(controllerClass).slash(MOST_WIDELY_USED_TAG_REL)
                .withRel(MOST_WIDELY_USED_TAG_URI);
    }
}
