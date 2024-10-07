package org.example.homeandgarden.controller;


import org.example.homeandgarden.dto.requestdto.CategoryRequestDto;
import org.example.homeandgarden.dto.responsedto.CategoryResponseDto;
import org.example.homeandgarden.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category controller", description = "Controller for managing product's categories")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/categories")
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Getting categories", description = "Provides functionality for getting  all product categories")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryResponseDto> getCategories() {
        return categoryService.getCategories();
    }

    @Operation(summary = "Deleting a category", description = "Provides functionality for deleting a product category")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCategory(@PathVariable
                                         @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                                         @Parameter(description = "Category identifier") Long id) {
        categoryService.deleteCategory(id);
    }

    @Operation(summary = "Inserting a category", description = "Provides functionality for inserting a new product category")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void insertCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        categoryService.insertCategory(categoryRequestDto);
    }

    @Operation(summary = "Updating a category", description = "Provides functionality for updating certain product category")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto,

                               @PathVariable
                               @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                               @Parameter(description = "Category identifier") Long id) {
        categoryService.updateCategory(categoryRequestDto, id);
    }
}