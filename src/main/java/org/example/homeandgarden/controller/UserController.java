package org.example.homeandgarden.controller;

import org.example.homeandgarden.dto.requestdto.*;
import org.example.homeandgarden.dto.responsedto.UserResponseDto;
import org.example.homeandgarden.service.*;
import org.example.homeandgarden.validation.CreateGroup;
import org.example.homeandgarden.validation.UpdateGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User controller", description = "Controller fo managing user's accounts")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "User registration", description = "Provides functionality for registering a new user")
    @Validated
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@RequestBody @Validated(CreateGroup.class) UserRequestDto userRequestDto) {
        userService.registerUser(userRequestDto);
    }

    @Operation(summary = "Administrator registration", description = "Provides functionality for registering a new administrator")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    @Validated
    @PostMapping("/registerAdmin")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAdmin(@RequestBody @Validated(CreateGroup.class) UserRequestDto userRequestDto) {
        userService.registerAdmin(userRequestDto);
    }

    @Operation(summary = "Updating user's account", description = "Provides functionality for updating information in user's account")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@PathVariable
                           @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                           @Parameter(description = "User identifier") Long id,

                           @RequestBody @Validated(UpdateGroup.class) UserRequestDto userUpdateDto) {

        userService.updateUser(id, userUpdateDto);
    }

    @Operation(summary = "Deleting user's account", description = "Provides functionality for deleting a user's account")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable
                           @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                           @Parameter(description = "Category identifier") Long id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Getting user by email", description = "Provides functionality for getting user by his email")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getUserByEmail(@RequestParam("email")
                                          @Pattern(regexp = "^[a-zA-Z0-9]+[\\w.+-]{2,30}@[a-zA-Z0-9-]{3,30}.[a-zA-Z0-9-.]{2,30}$", message = "Invalid email")
                                          @Parameter(description = "User's email") String email) {
        return userService.getUserByEmail(email);
    }
}

