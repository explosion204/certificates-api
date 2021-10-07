package com.epam.esm.controller;

import com.epam.esm.controller.hateoas.Hateoas;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.repository.PageContext;
import com.epam.esm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Hateoas
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieve all users.
     *
     * @return JSON {@link ResponseEntity} object that contains list of {@link UserDto}
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@ModelAttribute PageContext pageContext) {
        List<UserDto> users = userService.findAll(pageContext);
        return new ResponseEntity<>(users, OK);
    }

    /**
     * Retrieve user by its unique id.
     *
     * @param id user id
     * @throws EntityNotFoundException in case when user with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link UserDto} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") long id) {
        UserDto userDto = userService.findById(id);
        return new ResponseEntity<>(userDto, OK);
    }
}
