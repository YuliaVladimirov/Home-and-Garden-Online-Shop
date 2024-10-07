package org.example.homeandgarden.service;

import org.example.homeandgarden.dto.requestdto.CategoryRequestDto;
import org.example.homeandgarden.dto.responsedto.CategoryResponseDto;
import org.example.homeandgarden.entity.Category;
import org.example.homeandgarden.exception.DataAlreadyExistsException;
import org.example.homeandgarden.exception.DataNotFoundInDataBaseException;
import org.example.homeandgarden.mapper.Mappers;
import org.example.homeandgarden.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepositoryMock;

    @Mock
    private Mappers mappersMock;

    @InjectMocks
    private CategoryService categoryServiceMock;

    private CategoryResponseDto categoryResponseDto;
    private CategoryRequestDto categoryRequestDto, wrongCategoryRequestDto;
    private Category category;

    DataNotFoundInDataBaseException dataNotFoundInDataBaseException;
    DataAlreadyExistsException dataAlreadyExistsException;

    @BeforeEach
    void setUp() {

        category = new Category(
            1L,
            "Test category",
            null);

        categoryResponseDto = CategoryResponseDto.builder()
                .categoryId(1L)
                .name("Test category")
                .build();

        categoryRequestDto = CategoryRequestDto.builder()
                .name("Test category")
                .build();

        wrongCategoryRequestDto = CategoryRequestDto.builder()
                .name("Wrong category")
                .build();
    }

    @Test
    void getCategories() {
        when(categoryRepositoryMock.findAll()).thenReturn(List.of(category));
        when(mappersMock.convertToCategoryResponseDto(any(Category.class))).thenReturn(categoryResponseDto);
        List<CategoryResponseDto> actualList = categoryServiceMock.getCategories();
        verify(mappersMock, times(1)).convertToCategoryResponseDto(any(Category.class));

        assertFalse(actualList.isEmpty());
        assertEquals(category.getCategoryId(), actualList.getFirst().getCategoryId());
    }

    @Test
    void deleteCategoryById() {
        Long id = 1L;
        Long wrongId = 10L;

        when(categoryRepositoryMock.findById(id)).thenReturn(Optional.of(category));
        categoryServiceMock.deleteCategory(id);
        verify(categoryRepositoryMock,times(1)).findById(id);
        verify(categoryRepositoryMock,times(1)).deleteById(id);

        when(categoryRepositoryMock.findById(wrongId)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> categoryServiceMock.deleteCategory(wrongId));
        assertEquals("Category not found in database.", dataNotFoundInDataBaseException.getMessage());
    }

    @Test
    void insertCategories() {
        when(categoryRepositoryMock.findCategoryByName(categoryRequestDto.getName())).thenReturn(null);
        when(mappersMock.convertToCategory(any(CategoryRequestDto.class))).thenReturn(category);
        category.setCategoryId(0L);
        categoryServiceMock.insertCategory(categoryRequestDto);
        verify(categoryRepositoryMock, times(1)).save(any(Category.class));

        when(categoryRepositoryMock.findCategoryByName(wrongCategoryRequestDto.getName())).thenReturn(category);
        dataAlreadyExistsException = assertThrows(DataAlreadyExistsException.class,
                () -> categoryServiceMock.insertCategory(wrongCategoryRequestDto));
    }

    @Test
    void updateCategory() {
        Long id = 1L;
        Long wrongId = 10L;

        when(categoryRepositoryMock.findById(anyLong())).thenReturn(Optional.of(category));
        category.setName(categoryRequestDto.getName());
        categoryServiceMock.updateCategory(categoryRequestDto,id);
        verify(categoryRepositoryMock, times(1)).save(category);

        when(categoryRepositoryMock.findById(wrongId)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> categoryServiceMock.updateCategory(wrongCategoryRequestDto, wrongId));
        assertEquals("Category not found in database.", dataNotFoundInDataBaseException.getMessage());
    }
}