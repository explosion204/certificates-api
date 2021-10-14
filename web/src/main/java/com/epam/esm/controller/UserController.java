package com.epam.esm.controller;

import com.epam.esm.controller.hateoas.model.HateoasModel;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.controller.hateoas.model.ListHateoasModel;
import com.epam.esm.controller.model.ListModel;
import com.epam.esm.dto.TokenDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.exception.InvalidPageContextException;
import com.epam.esm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;
    private HateoasProvider<UserDto> modelHateoasProvider;
    private HateoasProvider<List<UserDto>> listHateoasProvider;


    public UserController(UserService userService, HateoasProvider<UserDto> modelHateoasProvider,
                HateoasProvider<List<UserDto>> listHateoasProvider) {
        this.userService = userService;
        this.modelHateoasProvider = modelHateoasProvider;
        this.listHateoasProvider = listHateoasProvider;
    }

    /**
     * Retrieve all users.
     *
     * @throws InvalidPageContextException if passed page or page size values are invalid
     * @return JSON {@link ResponseEntity} object that contains list of {@link ListHateoasModel} objects
     */
    @GetMapping
    public ResponseEntity<ListHateoasModel<UserDto>> getUsers(@RequestParam(required = false) Integer page,
                                                              @RequestParam(required = false) Integer pageSize) {
        List<UserDto> users = userService.findAll(PageContext.of(page, pageSize));
        ListHateoasModel<UserDto> model = ListHateoasModel.build(listHateoasProvider, users);
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
        HateoasModel<UserDto> model = HateoasModel.build(modelHateoasProvider, userDto);
        return new ResponseEntity<>(model, OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenDto> signup(@RequestBody UserDto userDto) {
        TokenDto tokenDto = userService.signup(userDto);
        return new ResponseEntity<>(tokenDto, CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody UserDto userDto) {
        TokenDto tokenDto = userService.login(userDto);
        return new ResponseEntity<>(tokenDto, OK);
    }
}
