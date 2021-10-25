package com.epam.esm.controller;

import com.epam.esm.controller.hateoas.model.HateoasModel;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.controller.hateoas.model.PageHateoasModel;
import com.epam.esm.dto.TokenDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.pagination.PageContext;
import com.epam.esm.exception.InvalidPageContextException;
import com.epam.esm.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.epam.esm.security.KeycloakAuthority.USERS_GET;
import static com.epam.esm.security.KeycloakAuthority.USERS_GET_BY_OWNER;
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
     * Access is allowed to users with 'users:get' authority (admin role).
     *
     * @throws InvalidPageContextException if passed page or page size values are invalid
     * @return JSON {@link ResponseEntity} object that contains list of {@link PageHateoasModel} objects
     */
    @GetMapping
    @PreAuthorize("hasAuthority('" + USERS_GET + "')")
    public ResponseEntity<PageHateoasModel<UserDto>> getUsers(@RequestParam(required = false) Integer page,
                                                              @RequestParam(required = false) Integer pageSize) {
        Page<UserDto> users = userService.findAll(PageContext.of(page, pageSize));
        PageHateoasModel<UserDto> model = PageHateoasModel.build(listHateoasProvider, users);
        return new ResponseEntity<>(model, OK);
    }

    /**
     * Retrieve user by its unique id.
     * Access is allowed to users with 'users:get' authority (admin role) OR
     * to the resource owner (with authority 'users:get_by_owner').
     *
     * @param id user id
     * @throws EntityNotFoundException in case when user with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('" + USERS_GET + "') or " +
            "hasAuthority('" + USERS_GET_BY_OWNER + "') and authentication.name eq T(String).valueOf(#id)"
    )
    public ResponseEntity<HateoasModel<UserDto>> getUser(@PathVariable("id") long id) {
        UserDto userDto = userService.findById(id);
        HateoasModel<UserDto> model = HateoasModel.build(modelHateoasProvider, userDto);
        return new ResponseEntity<>(model, OK);
    }

    /**
     * Create a new user.
     * Access is allowed to everyone.
     *
     * @param userDto {@link UserDto} instance
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @throws EntityAlreadyExistsException in case when user with specified username already exists
     * @return JSON {@link ResponseEntity} object that contains {@link TokenDto} object
     */
    @PostMapping("/signup")
    public ResponseEntity<TokenDto> signup(@RequestBody UserDto userDto) {
        TokenDto tokenDto = userService.signup(userDto);
        return new ResponseEntity<>(tokenDto, CREATED);
    }

    /**
     * Authenticate with provided credentials.
     * Access is allowed to everyone.
     *
     * @param userDto {@link UserDto} instance
     * @throws BadCredentialsException in case when provided credentials are wrong
     * @return JSON {@link ResponseEntity} object that contains {@link TokenDto} object
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody UserDto userDto) {
        TokenDto tokenDto = userService.login(userDto);
        return new ResponseEntity<>(tokenDto, OK);
    }
}
