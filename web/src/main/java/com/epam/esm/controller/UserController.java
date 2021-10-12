package com.epam.esm.controller;

import com.epam.esm.controller.hateoas.HateoasModel;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.repository.PageContext;
import com.epam.esm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;
    private HateoasProvider<UserDto> hateoasProvider;

    public UserController(UserService userService, HateoasProvider<UserDto> hateoasProvider) {
        this.userService = userService;
        this.hateoasProvider = hateoasProvider;
    }

    /**
     * Retrieve all users.
     *
     * @return JSON {@link ResponseEntity} object that contains list of {@link HateoasModel} objects
     */
    @GetMapping
    public ResponseEntity<List<HateoasModel>> getUsers(@ModelAttribute PageContext pageContext) {
        List<UserDto> users = userService.findAll(pageContext);
        List<HateoasModel> models = HateoasModel.build(hateoasProvider, users);
        return new ResponseEntity<>(models, OK);
    }

    /**
     * Retrieve user by its unique id.
     *
     * @param id user id
     * @throws EntityNotFoundException in case when user with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<HateoasModel> getUser(@PathVariable("id") long id) {
        UserDto userDto = userService.findById(id);
        HateoasModel model = HateoasModel.build(hateoasProvider, userDto);
        return new ResponseEntity<>(model, OK);
    }
}
