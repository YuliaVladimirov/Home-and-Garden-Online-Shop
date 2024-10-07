package org.example.homeandgarden.controller;

import org.example.homeandgarden.dto.requestdto.OrderItemRequestDto;
import org.example.homeandgarden.dto.requestdto.OrderRequestDto;
import org.example.homeandgarden.dto.responsedto.*;
import org.example.homeandgarden.entity.enums.DeliveryMethod;
import org.example.homeandgarden.entity.enums.Role;
import org.example.homeandgarden.entity.enums.Status;
import org.example.homeandgarden.security.config.SecurityConfig;
import org.example.homeandgarden.security.jwt.JwtAuthentication;
import org.example.homeandgarden.security.jwt.JwtProvider;
import org.example.homeandgarden.service.OrderService;
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

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderServiceMock;

    @MockBean
    private JwtProvider jwtProvider;


    private UserResponseDto userResponseDto;
    private OrderResponseDto orderResponseDto;
    private OrderItemResponseDto orderItemResponseDto;
    private ProductResponseDto productResponseDto;
    private Set<OrderItemResponseDto> orderItemResponseDtoSet = new HashSet<>();

    private OrderRequestDto orderRequestDto;
    private OrderItemRequestDto orderItemRequestDto;
    Set<OrderItemRequestDto> orderItemRequestDtoSet = new HashSet<>();


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

        orderResponseDto = OrderResponseDto.builder()
                .orderId(1L)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .deliveryAddress("Am Hofacker 64c, 9 OG, 32312, Leiteritzdorf, Sachsen-Anhalt, GERMANY")
                .contactPhone("+496921441")
                .deliveryMethod(DeliveryMethod.COURIER_DELIVERY)
                .status(Status.PAID)
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .orderItemsSet(null)
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

        orderItemResponseDto = OrderItemResponseDto.builder()
                .orderItemId(1L)
                .orderResponseDto(null)
                .productResponseDto(productResponseDto)
                .quantity(5)
                .build();

        orderItemResponseDtoSet.add(orderItemResponseDto);
        orderResponseDto.setOrderItemsSet(orderItemResponseDtoSet);

//RequestDto

        orderItemRequestDto = OrderItemRequestDto.builder()
                .productId(1L)
                .quantity(5)
                .build();
        orderItemRequestDtoSet.add(orderItemRequestDto);

        orderRequestDto = OrderRequestDto.builder()
                .orderItemsSet(orderItemRequestDtoSet)
                .deliveryAddress("Am Hofacker 64c, 9 OG, 32312, Leiteritzdorf, Sachsen-Anhalt, GERMANY")
                .deliveryMethod("COURIER_DELIVERY")
                .build();
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT", "ADMINISTRATOR"})
    void getOrderById() throws Exception {
        Long orderId = 1L;

        JwtAuthentication jwtAuthentication = new JwtAuthentication("arneoswald@example.com", List.of("CLIENT"));
        jwtAuthentication.setAuthenticated(true);

        when(orderServiceMock.getOrderById(orderId, "arneoswald@example.com")).thenReturn(orderResponseDto);
        this.mockMvc.perform(get("/orders/{orderId}", orderId)
                        .with(authentication(jwtAuthentication)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.contactPhone").value("+496921441"));

        verify(orderServiceMock, times(1)).getOrderById(orderId, "arneoswald@example.com");
    }

    @Test
    void shouldNotGetOrderById() throws Exception {
        Long orderId = 1L;
        when(orderServiceMock.getOrderById(orderId, null)).thenReturn(orderResponseDto);
        this.mockMvc.perform(get("/orders/{orderId}", orderId))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(orderServiceMock, never()).getOrderById(orderId, null);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT", "ADMINISTRATOR"})
    void getOrderHistory() throws Exception {

        JwtAuthentication jwtAuthentication = new JwtAuthentication("arneoswald@example.com", List.of("CLIENT"));
        jwtAuthentication.setAuthenticated(true);

        Set<OrderResponseDto> orderResponseDtoSet = new HashSet<>();
        orderResponseDtoSet.add(orderResponseDto);

        when(orderServiceMock.getOrderHistory("arneoswald@example.com")).thenReturn(orderResponseDtoSet);

        this.mockMvc.perform(get("/orders/history")
                        .with(authentication(jwtAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..orderId").value(1))
                .andExpect(jsonPath("$..contactPhone").value("+496921441"));

        verify(orderServiceMock, times(1)).getOrderHistory(jwtAuthentication.getEmail());
    }

    @Test
    void shouldNotGetOrderHistory() throws Exception {

        this.mockMvc.perform(get("/orders/history"))
                .andExpect(status().isForbidden());

        verify(orderServiceMock, never()).getOrderHistory(null);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT", "ADMINISTRATOR"})
    void insertOrder() throws Exception {

        JwtAuthentication jwtAuthentication = new JwtAuthentication("arneoswald@example.com", List.of("CLIENT"));
        jwtAuthentication.setAuthenticated(true);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto))
                        .with(authentication(jwtAuthentication)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(orderServiceMock, times(1)).insertOrder(orderRequestDto, jwtAuthentication.getEmail());
    }

    @Test
    void shouldNotInsertOrder() throws Exception {

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(orderServiceMock, never()).insertOrder(orderRequestDto, null);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void changeOrderStatus() throws Exception {
        Long orderId = 1L;
        String status = "PAID";

        JwtAuthentication jwtAuthentication = new JwtAuthentication("geraldrichter@example.com", List.of("ADMINISTRATOR"));
        jwtAuthentication.setAuthenticated(true);

        mockMvc.perform(put("/orders/change?id=1&status=PAID")
                        .with(authentication(jwtAuthentication)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(orderServiceMock, times(1)).changeOrderStatus(orderId, status);
    }

    @Test
    void shouldNotChangeOrderStatus() throws Exception {
        Long orderId = 1L;

        mockMvc.perform(put("/orders/change/{orderId}", orderId))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(orderServiceMock, never()).cancelOrder(orderId, null);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT", "ADMINISTRATOR"})
    void cancelOrder() throws Exception {
        Long orderId = 1L;

        JwtAuthentication jwtAuthentication = new JwtAuthentication("arneoswald@example.com", List.of("CLIENT"));
        jwtAuthentication.setAuthenticated(true);

        mockMvc.perform(put("/orders/cancel/{orderId}", orderId)
                        .with(authentication(jwtAuthentication)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(orderServiceMock, times(1)).cancelOrder(orderId, "arneoswald@example.com");
    }

    @Test
    void shouldNotCancelOrder() throws Exception {
        Long orderId = 1L;

        mockMvc.perform(put("/orders/cancel/{orderId}", orderId))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(orderServiceMock, never()).cancelOrder(orderId, null);
    }
}