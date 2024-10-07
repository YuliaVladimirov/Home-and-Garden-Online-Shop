package org.example.homeandgarden.controller;

import org.example.homeandgarden.dto.requestdto.CartItemRequestDto;
import org.example.homeandgarden.dto.responsedto.*;
import org.example.homeandgarden.entity.enums.Role;
import org.example.homeandgarden.security.config.SecurityConfig;
import org.example.homeandgarden.security.jwt.JwtAuthentication;
import org.example.homeandgarden.security.jwt.JwtProvider;
import org.example.homeandgarden.service.CartService;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartServiceMock;

    @MockBean
    private JwtProvider jwtProvider;


    private UserResponseDto userResponseDto;
    private CartResponseDto cartResponseDto;
    private CartItemResponseDto cartItemResponseDto;
    private ProductResponseDto productResponseDto;
    private Set<CartItemResponseDto> cartItemResponseDtoSet = new HashSet<>();

    private CartItemRequestDto cartItemRequestDto;

    @BeforeEach
    void setUp() {

//ResponseDto
        userResponseDto = UserResponseDto.builder()
                .userId(1L)
                .name("Arne Oswald")
                .email("arneoswald@example.com")
                .phone("+496151226")
                .passwordHash("Pass1$trong")
                .role(Role.CLIENT)
                .build();

        cartResponseDto = CartResponseDto.builder()
                .cartId(1L)
                .userResponseDto(userResponseDto)
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

        cartItemResponseDto = CartItemResponseDto.builder()
                .cartItemId(1L)
                .cartResponseDto(cartResponseDto)
                .productResponseDto(productResponseDto)
                .quantity(5)
                .build();

        cartItemResponseDtoSet.add(cartItemResponseDto);

//RequestDto
        cartItemRequestDto = CartItemRequestDto.builder()
                .productId(1L)
                .quantity(5)
                .build();

    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT","ADMINISTRATOR"})
    void getCartItems() throws Exception {
        when(cartServiceMock.getCartItems(anyString())).thenReturn(cartItemResponseDtoSet);

        JwtAuthentication jwtAuthentication = new JwtAuthentication("arneoswald@example.com", List.of("CLIENT"));
        jwtAuthentication.setAuthenticated(true);

        this.mockMvc.perform(get("/cart")
                .with(authentication(jwtAuthentication)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..cartItemId").value(1))
                .andExpect(jsonPath("$..product.productId").value(1));

        verify(cartServiceMock, times(1)).getCartItems(jwtAuthentication.getEmail());
    }

    @Test
    void shouldNotGetCartItems() throws Exception {

        this.mockMvc.perform(get("/cart"))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(cartServiceMock, never()).getCartItems(null);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT","ADMINISTRATOR"})
    void insertCartItem() throws Exception  {

        JwtAuthentication jwtAuthentication = new JwtAuthentication("arneoswald@example.com", List.of("CLIENT"));
        jwtAuthentication.setAuthenticated(true);

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemRequestDto))
                        .with(authentication(jwtAuthentication)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(cartServiceMock, times(1)).insertCartItem(cartItemRequestDto, jwtAuthentication.getEmail());
    }

    @Test
    void shouldNotInsertCartItem() throws Exception  {

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(cartServiceMock, never()).insertCartItem(cartItemRequestDto, null);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT","ADMINISTRATOR"})
    void deleteCarItemByProductId() throws Exception {

        Long productId = 1L;

        JwtAuthentication jwtAuthentication = new JwtAuthentication("arneoswald@example.com", List.of("CLIENT"));
        jwtAuthentication.setAuthenticated(true);

        mockMvc.perform(delete("/cart/{productId}", productId)
                        .with(authentication(jwtAuthentication)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(cartServiceMock, times(1)).deleteCartItemByProductId(jwtAuthentication.getEmail(), productId);
    }

    @Test
    void shouldNotDeleteCarItemByProductId() throws Exception {
        Long productId = 1L;

        mockMvc.perform(delete("/cart/{productId}", productId))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(cartServiceMock, never()).deleteCartItemByProductId(null, productId);
    }
}