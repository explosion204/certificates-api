package com.epam.esm.controller.hateoas;

import com.epam.esm.dto.IdentifiableDto;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

class LinkConstructor {
    private static final String LIST_REL = "list";
    private static final String CREATE_REL = "create";
    private static final String UPDATE_REL = "update";
    private static final String DELETE_REL = "delete";

    private LinkConstructor() {

    }

    static Link constructListLink(Class<?> controllerClass) {
        return linkTo(controllerClass)
                .withRel(LIST_REL);
    }

    static <T extends RepresentationModel<T>> Link constructLinkWithId(Class<?> controllerClass,
                IdentifiableDto<T> model, String rel) {
        return linkTo(controllerClass)
                .slash(model.getId())
                .withRel(rel);
    }

    static <T extends RepresentationModel<T>> Link constructSelfLink(Class<?> controllerClass,
                IdentifiableDto<T> model) {
        return linkTo(controllerClass)
                .slash(model.getId())
                .withSelfRel();
    }

    static <T extends RepresentationModel<T>> Link constructCreateLink(Class<?> controllerClass,
                IdentifiableDto<T> model) {
        return constructLinkWithId(controllerClass, model, CREATE_REL);
    }

    static <T extends RepresentationModel<T>> Link constructUpdateLink(Class<?> controllerClass,
                IdentifiableDto<T> model) {
        return constructLinkWithId(controllerClass, model, UPDATE_REL);
    }

    static <T extends RepresentationModel<T>> Link constructDeleteLink(Class<?> controllerClass,
                IdentifiableDto<T> model) {
        return constructLinkWithId(controllerClass, model, DELETE_REL);
    }
}
