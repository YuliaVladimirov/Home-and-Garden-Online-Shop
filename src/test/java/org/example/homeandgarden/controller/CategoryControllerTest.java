package org.example.homeandgarden.controller;

import org.example.homeandgarden.dto.requestdto.CategoryRequestDto;
import org.example.homeandgarden.dto.responsedto.CategoryResponseDto;
import org.example.homeandgarden.security.config.SecurityConfig;
import org.example.homeandgarden.security.jwt.JwtProvider;
import org.example.homeandgarden.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryServiceMock;

    @MockBean
    private JwtProvider jwtProvider;


    private CategoryResponseDto categoryResponseDto;
    private CategoryRequestDto categoryRequestDto;


    @BeforeEach
    void setUp() {

        categoryResponseDto = CategoryResponseDto.builder()
                .categoryId(1L)
                .name("Name")
                .build();
        categoryRequestDto = CategoryRequestDto.builder()
                .name("Name")
                .build();
    }

    @Test
    void getCategories() throws Exception {
        when(categoryServiceMock.getCategories()).thenReturn(List.of(categoryResponseDto));
        this.mockMvc.perform(get("/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..categoryId").value(1))
                .andExpect(jsonPath("$..name").value("Name"));

        verify(categoryServiceMock, times(1)).getCategories();
    }


    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void deleteCategory() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/categories/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());

        verify(categoryServiceMock, times(1)).deleteCategory(id);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotDeleteCategory() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/categories/{id}", id))
                .andDo(print())
                .andExpect(status().isForbidden());
        verify(categoryServiceMock, never()).deleteCategory(id);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void insertCategory() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(categoryServiceMock, times(1)).insertCategory(categoryRequestDto);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotInsertCategory() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(categoryServiceMock, never()).insertCategory(categoryRequestDto);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void updateCategory() throws Exception {
        Long id =1L;
        this.mockMvc.perform(put("/categories/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(categoryServiceMock, times(1)).updateCategory(categoryRequestDto, id);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotUpdateCategory() throws Exception {
        Long id =1L;
        this.mockMvc.perform(put("/categories/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(categoryServiceMock, never()).updateCategory(categoryRequestDto, id);
    }
}