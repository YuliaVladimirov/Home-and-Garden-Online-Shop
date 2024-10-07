package org.example.homeandgarden.controller;

import org.example.homeandgarden.dto.requestdto.FavoriteRequestDto;
import org.example.homeandgarden.dto.responsedto.*;
import org.example.homeandgarden.entity.enums.Role;
import org.example.homeandgarden.security.config.SecurityConfig;
import org.example.homeandgarden.security.jwt.JwtAuthentication;
import org.example.homeandgarden.security.jwt.JwtProvider;
import org.example.homeandgarden.service.FavoriteService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(FavoriteController.class)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FavoriteService favoriteServiceMock;

    @MockBean
    private JwtProvider jwtProvider;


    private UserResponseDto userResponseDto;
    private ProductResponseDto productResponseDto;
    private FavoriteResponseDto favoriteResponseDto;
    private Set<FavoriteResponseDto> favoriteResponseDtoSet = new HashSet<>();

    private FavoriteRequestDto favoriteRequestDto;

    @BeforeEach
    void setUp() {

//ResponseDto

        userResponseDto = UserResponseDto.builder()
                .userId(1L)
                .name("Arne Oswald")
                .email("arneoswald@example.com")
                .phone("+496151226")
                .passwordHash("ClientPass1$trong")
                .role(Role.CLIENT)
                .build();

        productResponseDto = ProductResponseDto.builder()
                .productId(1L)
                .name("Name")
                .description("Description")
                .price(new BigDecimal("100.00"))
                .imageUrl("http://localhost/img/1.jpg")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .categoryResponseDto(new CategoryResponseDto(1L, "Category"))
                .build();

        favoriteResponseDto = FavoriteResponseDto.builder()
                .favoriteId(1L)
                .productResponseDto(productResponseDto)
                .userResponseDto(userResponseDto)
                .build();

        favoriteResponseDtoSet.add(favoriteResponseDto);

        //RequestDto

        favoriteRequestDto = FavoriteRequestDto.builder()
                .productId(1L)
                .build();
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT", "ADMINISTRATOR"})
    void getFavorites() throws Exception {

        when(favoriteServiceMock.getFavorites(any(String.class))).thenReturn(favoriteResponseDtoSet);

        JwtAuthentication jwtAuthentication = new JwtAuthentication("arneoswald@example.com", List.of("CLIENT"));
        jwtAuthentication.setAuthenticated(true);

        this.mockMvc.perform(get("/favorites")
                        .with(authentication(jwtAuthentication)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..favoriteId").value(1));

        verify(favoriteServiceMock, times(1)).getFavorites(jwtAuthentication.getEmail());
    }

    @Test
    void shouldNotGetFavorites() throws Exception {

        this.mockMvc.perform(get("/favorites"))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(favoriteServiceMock, never()).getFavorites(null);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT", "ADMINISTRATOR"})
    void insertFavorite() throws Exception {

        JwtAuthentication jwtAuthentication = new JwtAuthentication("arneoswald@example.com", List.of("CLIENT"));
        jwtAuthentication.setAuthenticated(true);

        mockMvc.perform(post("/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(favoriteRequestDto))
                        .with(authentication(jwtAuthentication)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(favoriteServiceMock, times(1)).insertFavorite(favoriteRequestDto, jwtAuthentication.getEmail());
    }

    @Test
    void shouldNotInsertFavorite() throws Exception {

        mockMvc.perform(post("/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(favoriteRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(favoriteServiceMock, never()).insertFavorite(favoriteRequestDto, null);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT", "ADMINISTRATOR"})
    void deleteFavoriteByProductId() throws Exception {

        Long productId = 1L;

        JwtAuthentication jwtAuthentication = new JwtAuthentication("arneoswald@example.com", List.of("CLIENT"));
        jwtAuthentication.setAuthenticated(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/favorites/{productId}", productId)
                        .with(authentication(jwtAuthentication)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(favoriteServiceMock, times(1)).deleteFavoriteByProductId( jwtAuthentication.getEmail(), productId);
    }

    @Test
    void shouldNotDeleteFavoriteByProductId() throws Exception {

        Long productId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/favorites/{productId}", productId))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(favoriteServiceMock, never()).deleteFavoriteByProductId( null, productId);
    }
}