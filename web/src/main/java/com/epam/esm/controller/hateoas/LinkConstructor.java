package com.epam.esm.controller.hateoas;

import com.epam.esm.dto.IdentifiableDto;
import org.springframework.hateoas.Link;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class LinkConstructor {
    private static final String ALL_RESOURCES_REL = "allResources";

    private LinkConstructor() {

    }

    public static <T extends IdentifiableDto> Link constructSelfLink(Class<?> controllerClass, T model) {
        return linkTo(controllerClass)
                .slash(model.getId())
                .withSelfRel();
    }

    public static Link constructControllerLink(Class<?> controllerClass) {
        return linkTo(controllerClass)
                .withRel(ALL_RESOURCES_REL);
    }

    public static Link constructControllerLinkWithId(Class<?> controllerClass, long id, String rel) {
        return linkTo(controllerClass)
                .slash(id)
                .withRel(rel);
    }
}
