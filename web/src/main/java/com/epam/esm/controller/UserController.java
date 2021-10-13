package com.epam.esm.controller;

import com.epam.esm.controller.model.HateoasModel;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.controller.model.ListModel;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.exception.InvalidPageContextException;
import com.epam.esm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @throws InvalidPageContextException if passed page or page size values are invalid
     * @return JSON {@link ResponseEntity} object that contains list of {@link HateoasModel} objects
     */
    @GetMapping
    public ResponseEntity<ListModel<UserDto>> getUsers(@RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) Integer pageSize) {
        List<UserDto> users = userService.findAll(PageContext.of(page, pageSize));
        ListModel<UserDto> model = ListModel.build(users);
        return new ResponseEntity<>(model, OK);
    }

    /**
     * Retrieve user by its unique id.
     *
     * @param id user id
     * @throws EntityNotFoundException in case when user with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<HateoasModel<UserDto>> getUser(@PathVariable("id") long id) {
        UserDto userDto = userService.findById(id);
        HateoasModel<UserDto> model = HateoasModel.build(hateoasProvider, userDto);
        return new ResponseEntity<>(model, OK);
    }
}
