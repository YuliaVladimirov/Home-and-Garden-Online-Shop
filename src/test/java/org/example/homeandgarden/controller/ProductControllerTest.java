package org.example.homeandgarden.controller;

import org.example.homeandgarden.dto.querydto.ProductCountDto;
import org.example.homeandgarden.dto.querydto.ProductPendingDto;
import org.example.homeandgarden.dto.querydto.ProductProfitDto;
import org.example.homeandgarden.dto.requestdto.ProductRequestDto;
import org.example.homeandgarden.dto.responsedto.CategoryResponseDto;
import org.example.homeandgarden.dto.responsedto.ProductResponseDto;
import org.example.homeandgarden.security.config.SecurityConfig;
import org.example.homeandgarden.security.jwt.JwtProvider;
import org.example.homeandgarden.service.ProductService;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productServiceMock;

    @MockBean
    private JwtProvider jwtProvider;


    private ProductResponseDto productResponseDto;
    private ProductRequestDto productRequestDto;

    @BeforeEach
    void setUp() {

        productResponseDto = ProductResponseDto.builder()
                .productId(1L)
                .name("Name")
                .description("Description")
                .price(new BigDecimal("100.00"))
                .discountPrice(new BigDecimal("100.00"))
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .imageUrl("https://example.com/images/deroma_white_garden_pot.jpg")
                .categoryResponseDto(CategoryResponseDto.builder()
                        .categoryId(1L)
                        .name("Test category")
                        .build())
                .build();
        productRequestDto = ProductRequestDto.builder()
                .name("Name")
                .description("Description")
                .price(new BigDecimal("101.00"))
                .imageUrl("https://example.com/images/magic_garden_seeds.jpg")
                .category("Test category")
                .build();
    }

    @Test
    void getProduct() throws Exception {
        Long id = 1L;
        when(productServiceMock.getProduct(id)).thenReturn(productResponseDto);
        this.mockMvc.perform(get("/products/{id}", id))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productResponseDto.getProductId()))
                .andExpect(jsonPath("$.name").value(productResponseDto.getName()));

        verify(productServiceMock, times(1)).getProduct(id);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void deleteProduct() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/products/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
        verify(productServiceMock, times(1)).deleteProduct(id);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotDeleteProduct() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/products/{id}", id))
                .andDo(print())
                .andExpect(status().isForbidden());
        verify(productServiceMock, never()).deleteProduct(id);
    }


    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void insertProduct() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(productServiceMock, times(1)).insertProduct(productRequestDto);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotInsertProduct() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(productServiceMock, never()).insertProduct(productRequestDto);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void updateProduct() throws Exception {
        Long id = 1L;
        mockMvc.perform(put("/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(productServiceMock, times(1)).updateProduct(productRequestDto,id);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotUpdateProduct() throws Exception {
        Long id = 1L;
        mockMvc.perform(put("/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(productServiceMock, never()).updateProduct(productRequestDto,id);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void setDiscountPrice() throws Exception {
        mockMvc.perform(put("/products?id=1&discountPrice=2.55"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(productServiceMock, times(1)).setDiscountPrice(1L, BigDecimal.valueOf(2.55));
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotSetDiscountPrice() throws Exception {
        mockMvc.perform(put("/products?id=1&discountPrice=2.55"))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(productServiceMock, never()).setDiscountPrice(1L, BigDecimal.valueOf(2.55));
    }


    @Test
    void getMaxDiscountProduct() throws Exception {
        when(productServiceMock.getMaxDiscountProduct()).thenReturn(productResponseDto);
        mockMvc.perform(get("/products/maxDiscount"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productResponseDto.getProductId()))
                .andExpect(jsonPath("$.name").value(productResponseDto.getName()));

        verify(productServiceMock, times(1)).getMaxDiscountProduct();
    }



    @Test
    void getProducts() throws Exception {
        Long categoryId = 1L;
        BigDecimal minPrice = BigDecimal.valueOf(0.0);
        BigDecimal maxPrice = BigDecimal.valueOf(100.0);
        Boolean hasDiscount = true;
        String sortValues = "name,asc";

        when(productServiceMock.getProductsByFilter(categoryId, minPrice, maxPrice, hasDiscount, sortValues))
                .thenReturn((List.of(productResponseDto)));
        this.mockMvc.perform(get("/products?category=1&minPrice=0.0&maxPrice=100.0&discount=true&sort=name,asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..productId").value(1))
                .andExpect(jsonPath("$..price").value(100.0));

        verify(productServiceMock, times(1)).getProductsByFilter(categoryId, minPrice, maxPrice, hasDiscount, sortValues);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void getTop10Products() throws Exception {
        String status = "PAID";

        ProductCountDto productCountDto = ProductCountDto.builder()
                .productId(1L)
                .name("Test Name")
                .count(2)
                .sum(BigDecimal.valueOf(1.0))
                .build();

        when(productServiceMock.getTop10Products(status)).thenReturn(List.of(productCountDto));
        this.mockMvc.perform(get("/products/top10?status=PAID"))
                .andDo(print())
                .andExpect(status().isOk()).andExpect(jsonPath("$..productId").value(1))
                .andExpect(jsonPath("$..sum").value(1.0));

        verify(productServiceMock, times(1)).getTop10Products(status);

    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotGetTop10Products() throws Exception {
        String status = "PAID";

        ProductCountDto productCountDto = ProductCountDto.builder()
                .productId(1L)
                .name("Test Name")
                .count(2)
                .sum(BigDecimal.valueOf(1.0))
                .build();

        when(productServiceMock.getTop10Products(status)).thenReturn(List.of(productCountDto));
        this.mockMvc.perform(get("/products/top10?status=PAID"))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(productServiceMock, never()).getTop10Products(status);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void getProductPending() throws Exception {
        Integer day = 55;

        ProductPendingDto productPendingDto = ProductPendingDto.builder()
                .productId(1L)
                .name("Test name")
                .count(23)
                .build();

        when(productServiceMock.findProductPending(day)).thenReturn(List.of(productPendingDto));
        this.mockMvc.perform(get("/products/pending?day=55"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk()).andExpect(jsonPath("$..productId").value(1))
                .andExpect(jsonPath("$..count").value(23));

        verify(productServiceMock, times(1)).findProductPending(day);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotGetProductPending() throws Exception {
        Integer day = 55;

        ProductPendingDto productPendingDto = ProductPendingDto.builder()
                .productId(1L)
                .name("Test name")
                .count(23)
                .build();

        when(productServiceMock.findProductPending(day)).thenReturn(List.of(productPendingDto));
        this.mockMvc.perform(get("/products/pending?day=55"))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(productServiceMock, never()).findProductPending(day);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"ADMINISTRATOR"})
    void getProfitByPeriod() throws Exception {
        String period = "WEEK";
        Integer value = 55;

        ProductProfitDto productProfitDto = ProductProfitDto.builder()
                .period(period)
                .sum(BigDecimal.valueOf(234.33))
                .build();

        when(productServiceMock.findProductProfit(period, value)).thenReturn(List.of(productProfitDto));
        this.mockMvc.perform(get("/products/profit?period=WEEK&value=55"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk()).andExpect(jsonPath("$..period").value(period))
                .andExpect(jsonPath("$..sum").value(234.33));

        verify(productServiceMock, times(1)).findProductProfit(period, value);
    }

    @Test
    @WithMockUser(username = "Test User", roles = {"CLIENT"})
    void shouldNotGetProfitByPeriod() throws Exception {
        String period = "WEEK";
        Integer value = 55;

        ProductProfitDto productProfitDto = ProductProfitDto.builder()
                .period(period)
                .sum(BigDecimal.valueOf(234.33))
                .build();

        when(productServiceMock.findProductProfit(period, value)).thenReturn(List.of(productProfitDto));
        this.mockMvc.perform(get("/products/profit?period=WEEK&value=55"))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(productServiceMock, never()).findProductProfit(period, value);
    }
}