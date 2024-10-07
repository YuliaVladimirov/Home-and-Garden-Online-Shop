package org.example.homeandgarden.controller;

import org.example.homeandgarden.dto.requestdto.FavoriteRequestDto;
import org.example.homeandgarden.dto.responsedto.FavoriteResponseDto;
import org.example.homeandgarden.security.jwt.JwtAuthentication;
import org.example.homeandgarden.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Favorite controller", description = "Controller for managing user's favorite products")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Operation(summary = "Getting user's favorites", description = "Provides functionality for getting  all user's favorite products")
    @SecurityRequirement(name = "JWT")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Set<FavoriteResponseDto> getFavorites() {
        final JwtAuthentication jwtInfoToken = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String email = jwtInfoToken.getEmail();

        return favoriteService.getFavorites(email);
    }

    @Operation(summary = "Inserting a favorite", description = "Provides functionality for inserting a new favorite product for the user")
    @SecurityRequirement(name = "JWT")
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public void insertFavorite(@RequestBody @Valid FavoriteRequestDto favoriteRequestDto) {

        final JwtAuthentication jwtInfoToken = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String email = jwtInfoToken.getEmail();

        favoriteService.insertFavorite(favoriteRequestDto, email);
    }

    @Operation(summary = "Deleting a favorite", description = "Provides functionality for deleting a favorite product from user's favorites list")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping(value = "/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFavoriteByProductId(@PathVariable("productId")
                                          @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                                          @Parameter(description = "Product identifier") Long productId) {

        final JwtAuthentication jwtInfoToken = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String email = jwtInfoToken.getEmail();

        favoriteService.deleteFavoriteByProductId(email, productId);
    }
}
