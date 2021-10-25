package com.epam.esm.controller.hateoas.impl;

import com.epam.esm.controller.UserController;
import com.epam.esm.controller.hateoas.ModelHateoasProvider;
import com.epam.esm.dto.UserDto;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.epam.esm.controller.hateoas.impl.ResourceRelName.ALL_USERS_REL;

@Component
public class UserHateoasProvider extends ModelHateoasProvider<UserDto> {
    @Override
    protected List<Link> addSpecificLinks(List<Link> baseLinks, UserDto model) {
        return baseLinks;
    }

    @Override
    protected Class<?> getControllerClass() {
        return UserController.class;
    }

    @Override
    protected String getAllResourcesRel() {
        return ALL_USERS_REL;
    }
}
