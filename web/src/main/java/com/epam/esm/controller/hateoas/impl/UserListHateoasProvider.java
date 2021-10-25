package com.epam.esm.controller.hateoas.impl;

import com.epam.esm.controller.UserController;
import com.epam.esm.controller.hateoas.ListHateoasProvider;
import com.epam.esm.dto.UserDto;
import org.springframework.stereotype.Component;

import static com.epam.esm.controller.hateoas.impl.ResourceRelName.ALL_USERS_REL;

@Component
public class UserListHateoasProvider extends ListHateoasProvider<UserDto> {
    @Override
    protected Class<?> getControllerClass() {
        return UserController.class;
    }

    @Override
    protected String getAllResourcesRel() {
        return ALL_USERS_REL;
    }
}
