package com.epam.esm.controller.hateoas.impl;

import com.epam.esm.controller.UserController;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.controller.hateoas.LinkConstructor;
import com.epam.esm.dto.UserDto;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserHateoasProvider implements HateoasProvider<UserDto> {
    private static final String ALL_USERS_REL = "allUsers";

    @Override
    public List<Link> provide(UserDto model) {
        Class<?> controllerClass = UserController.class;

        Link selfLink = LinkConstructor.constructSelfLink(controllerClass, model);
        Link allResourcesLink = LinkConstructor.constructControllerLink(controllerClass, ALL_USERS_REL);

        return List.of(selfLink, allResourcesLink);
    }
}
