package org.example.homeandgarden.service;

import org.example.homeandgarden.dto.querydto.ProductCountDto;
import org.example.homeandgarden.dto.querydto.ProductPendingDto;
import org.example.homeandgarden.dto.querydto.ProductProfitDto;
import org.example.homeandgarden.dto.requestdto.ProductRequestDto;
import org.example.homeandgarden.dto.responsedto.CategoryResponseDto;
import org.example.homeandgarden.dto.responsedto.ProductResponseDto;
import org.example.homeandgarden.entity.Category;
import org.example.homeandgarden.entity.Product;
import org.example.homeandgarden.entity.query.ProductCountInterface;
import org.example.homeandgarden.entity.query.ProductPendingInterface;
import org.example.homeandgarden.entity.query.ProductProfitInterface;
import org.example.homeandgarden.exception.DataNotFoundInDataBaseException;
import org.example.homeandgarden.mapper.Mappers;
import org.example.homeandgarden.repository.CategoryRepository;
import org.example.homeandgarden.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepositoryMock;
    @Mock
    private CategoryRepository categoryRepositoryMock;

    @Mock
    private Mappers mappersMock;

    @InjectMocks
    private ProductService productServiceMock;

    DataNotFoundInDataBaseException dataNotFoundInDataBaseException;

    private ProductResponseDto productResponseDto;
    private ProductRequestDto productRequestDto, wrongProductRequestDto;
    private Product product, productToInsert;
    private Category category;

    @BeforeEach
    void setUp() {

       productResponseDto = ProductResponseDto.builder()
                .productId(1L)
                .name("Name")
                .description("Description")
                .price(new BigDecimal("100.00"))
                .imageUrl("http://localhost/img/1.jpg")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .categoryResponseDto(new CategoryResponseDto(1L,"Category"))
                .build();

        product = new Product(1L,
                "Name",
                "Description",
                new BigDecimal("100.00"),
                new BigDecimal("0.00"),
                "http://localhost/img/1.jpg",
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                new Category(1L,"Category",null),
                null,
                null,
                null);

        productToInsert = new Product(null,
                "Name",
                "Description",
                new BigDecimal("100.00"),
                new BigDecimal("0.00"),
                "http://localhost/img/1.jpg",
                null,
                null,
                new Category(1L,"Category",null),
                null,
                null,
                null);

        category = new Category(1L,
                "Category",
                null);

        productRequestDto = ProductRequestDto.builder()
                .name("Name")
                .description("Description")
                .price(new BigDecimal("100.00"))
                .imageUrl("http://localhost/img/1.jpg")
                .category("Category")
                .build();

        wrongProductRequestDto = ProductRequestDto.builder()
                .name("Name")
                .description("Description")
                .price(new BigDecimal("100.00"))
                .imageUrl("http://localhost/img/1.jpg")
                .category("WrongCategory")
                .build();
    }

    @Test
    void getProduct() {
        Long id = 1L;
        Long wrongId = 58L;

        when(productRepositoryMock.findById(anyLong())).thenReturn(Optional.of(product));
        when(mappersMock.convertToProductResponseDto(any(Product.class))).thenReturn(productResponseDto);
        ProductResponseDto actualProductResponseDto = productServiceMock.getProduct(id);

        verify(mappersMock, times(1)).convertToProductResponseDto(any(Product.class));
        verify(productRepositoryMock, times(1)).findById(id);
        assertEquals(productResponseDto.getProductId(), actualProductResponseDto.getProductId());

        when(productRepositoryMock.findById(wrongId)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> productServiceMock.getProduct(wrongId));
        assertEquals("Product not found in database.", dataNotFoundInDataBaseException.getMessage());

    }

    @Test
    void deleteProductById() {
        Long id = 1L;
        Long wrongId = 35L;

        when(productRepositoryMock.findById(id)).thenReturn(Optional.of(product));

        productServiceMock.deleteProduct(id);

        verify(productRepositoryMock,times(1)).deleteById(product.getProductId());

        when(productRepositoryMock.findById(wrongId)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> productServiceMock.deleteProduct(wrongId));
        assertEquals("Product not found in database.", dataNotFoundInDataBaseException.getMessage());
    }

    @Test
    void insertProduct() {
        when(categoryRepositoryMock.findCategoryByName(productRequestDto.getCategory())).thenReturn(category);
        when(mappersMock.convertToProduct(any(ProductRequestDto.class))).thenReturn(productToInsert);

        productServiceMock.insertProduct(productRequestDto);

        verify(mappersMock, times(1)).convertToProduct(any(ProductRequestDto.class));
        verify(productRepositoryMock, times(1)).save(productToInsert);

        when(categoryRepositoryMock.findCategoryByName(wrongProductRequestDto.getCategory())).thenReturn(null);
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> productServiceMock.insertProduct(wrongProductRequestDto));
        assertEquals("Category not found in database.", dataNotFoundInDataBaseException.getMessage());
    }

    @Test
    void updateProduct() {
        Long id = 1L;
        Long wrongId = 58L;

        when(productRepositoryMock.findById(id)).thenReturn(Optional.of(product));
        when(categoryRepositoryMock.findCategoryByName(anyString())).thenReturn(category);

        productServiceMock.updateProduct(productRequestDto,id);

        verify(productRepositoryMock, times(1)).save(any(Product.class));

        when(productRepositoryMock.findById(wrongId)).thenReturn(Optional.empty());
        when(categoryRepositoryMock.findCategoryByName(wrongProductRequestDto.getCategory())).thenReturn(null);
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> productServiceMock.updateProduct(productRequestDto, wrongId));
        assertEquals("Product not found in database.", dataNotFoundInDataBaseException.getMessage());

        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> productServiceMock.updateProduct(wrongProductRequestDto, id));
        assertEquals("Category not found in database.", dataNotFoundInDataBaseException.getMessage());
    }

    @Test
    void setDiscountPrice(){
        Long id = 1L;
        Long wrongId = 58L;
        BigDecimal discountPrice = new BigDecimal(2.55);

        when(productRepositoryMock.findById(id)).thenReturn(Optional.of(product));
        product.setDiscountPrice(discountPrice);

        productServiceMock.setDiscountPrice(id, discountPrice);

        verify(productRepositoryMock, times(1)).save(any(Product.class));


        when(productRepositoryMock.findById(wrongId)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> productServiceMock.setDiscountPrice(wrongId, discountPrice));
        assertEquals("Product not found in database.", dataNotFoundInDataBaseException.getMessage());
    }

    @Test
    void getMaxDiscountProduct(){
        List<Product> maxDiscountProductList = List.of(product);
        when(productRepositoryMock.getMaxDiscountProduct()).thenReturn(maxDiscountProductList);
        when(mappersMock.convertToProductResponseDto(any(Product.class))).thenReturn(productResponseDto);

        productServiceMock.getMaxDiscountProduct();

        verify(productRepositoryMock, times(1)).getMaxDiscountProduct();
        verify(mappersMock, times(1)).convertToProductResponseDto(any(Product.class));
    }


    @Test
    void getTop10Products() {
        class MockProductCount implements ProductCountInterface {
            private Long productId;
            private String name;
            private String status;
            private Integer count;
            private BigDecimal sum;

            public MockProductCount(Long productId, String name, String status, Integer count, BigDecimal sum) {
                this.productId = productId;
                this.name = name;
                this.status = status;
                this.count = count;
                this.sum = sum;
            }

            @Override
            public Long getProductId() {
                return productId;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getStatus() {
                return status;
            }

            @Override
            public Integer getCount() {
                return count;
            }

            @Override
            public BigDecimal getSum() {
                return sum;
            }
        }
        ProductCountDto productCountDto =ProductCountDto.builder().productId(1L).name("Test name").status("PAID").count(2).sum(BigDecimal.valueOf(1.0)).build();
        String sort = "Price";
        ProductCountInterface productCountMock = new MockProductCount(1L, "Test name", "PAID",2, BigDecimal.valueOf(1.0));

        List<ProductCountInterface> productCountInterfaceList = List.of(productCountMock);

        when(productRepositoryMock.findTop10Products(anyString())).thenReturn(productCountInterfaceList);
        when(mappersMock.convertToProductCountDto(any(ProductCountInterface.class))).thenReturn(productCountDto);

        List <ProductCountDto> actualProductCountDto = productServiceMock.getTop10Products(sort);

        verify(productRepositoryMock, times(1)).findTop10Products(sort);
        assertEquals(1, actualProductCountDto.size());
        assertNotNull(actualProductCountDto.getFirst());
        assertEquals(productCountDto.getProductId(), actualProductCountDto.getFirst().getProductId());
        assertEquals(productCountDto.getName(), actualProductCountDto.getFirst().getName());
        assertEquals(productCountDto.getStatus(), actualProductCountDto.getFirst().getStatus());
        assertEquals(productCountDto.getCount(), actualProductCountDto.getFirst().getCount());
        assertEquals(productCountDto.getSum(), actualProductCountDto.getFirst().getSum());
    }

    @Test
    void getProductsByFilter() {
        Boolean hasCategory = true;
        Long categoryId = 1L;
        BigDecimal minPrice = BigDecimal.valueOf(0.00);
        BigDecimal maxPrice = BigDecimal.valueOf(100.00);
        Boolean hasDiscount = true;
        String sort = "name,asc";
        Sort sortObject = orderBy("name", true);
        when(productRepositoryMock.findProductsByFilter(hasCategory,categoryId,minPrice,maxPrice,hasDiscount,sortObject)).thenReturn(List.of(product));
        when(mappersMock.convertToProductResponseDto(product)).thenReturn(productResponseDto);

        List<ProductResponseDto> actualProductResponseDto = productServiceMock.getProductsByFilter(categoryId, minPrice, maxPrice, hasDiscount, sort);

        assertFalse(actualProductResponseDto.isEmpty());
        verify(productRepositoryMock, times(1)).findProductsByFilter(hasCategory,categoryId,minPrice,maxPrice,hasDiscount,sortObject);
        verify(mappersMock, times(1)).convertToProductResponseDto(product);
        assertEquals(product.getProductId(),actualProductResponseDto.getFirst().getProductId());
    }

    @Test
    void findProductPending() {
        class MockProductPending implements ProductPendingInterface {
            private Long productId;
            private String name;
            private Integer count;
            private String status;

            public MockProductPending(Long productId, String name, Integer count, String status) {
                this.productId = productId;
                this.name = name;
                this.count = count;
                this.status = status;
            }

            @Override
            public Long getProductId() {
                return productId;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public Integer getCount() {
                return count;
            }

            @Override
            public String getStatus() {
                return status;
            }
        }
        ProductPendingDto productPendingDto = ProductPendingDto.builder().productId(1L).name("Test name").count(2).status("PENDING").build();
        Integer day = 5;
        ProductPendingInterface productPendingMock = new MockProductPending(1L,"Test name",2,"PENDING");
        List<ProductPendingInterface> productPendingInterfaceList = List.of(productPendingMock);
        when(productRepositoryMock.findProductPending(anyInt())).thenReturn(productPendingInterfaceList);
        when(mappersMock.convertToProductPendingDto(any(ProductPendingInterface.class))).thenReturn(productPendingDto);
        List <ProductPendingDto> actualProductPendingDto = productServiceMock.findProductPending(day);
        verify(productRepositoryMock, times(1)).findProductPending(day);
        assertEquals(1, actualProductPendingDto.size());
        assertNotNull(actualProductPendingDto.getFirst());
        assertEquals(productPendingDto.getProductId(), actualProductPendingDto.getFirst().getProductId());
        assertEquals(productPendingDto.getName(), actualProductPendingDto.getFirst().getName());
        assertEquals(productPendingDto.getCount(), actualProductPendingDto.getFirst().getCount());
        assertEquals(productPendingDto.getStatus(), actualProductPendingDto.getFirst().getStatus());
    }

    @Test
    void findProductProfit() {
        class MockProductProfit implements ProductProfitInterface {
            private String period;
            private BigDecimal sum;

            public MockProductProfit(String period, BigDecimal sum) {
                this.period = period;
                this.sum = sum;
            }
            @Override
            public String getPeriod() {
                return period;
            }
            @Override
            public BigDecimal getSum() {
                return sum;
            }
        }
        ProductProfitDto productProfitDto = ProductProfitDto.builder().period("WEEK").sum(BigDecimal.valueOf(22.0)).build();
        String period = "WEEK";
        Integer interval = 5;
        ProductProfitInterface productProfitInterface = new MockProductProfit("WEEK",BigDecimal.valueOf(22.0));
        List<ProductProfitInterface> productProfitInterfaceList = List.of(productProfitInterface);
        when(productRepositoryMock.findProfitByPeriod(anyString(),anyInt())).thenReturn(productProfitInterfaceList);
        when(mappersMock.convertToProductProfitDto(any(ProductProfitInterface.class))).thenReturn(productProfitDto);

        List <ProductProfitDto> actualProductProfitDto = productServiceMock.findProductProfit(period,interval);
        verify(productRepositoryMock, times(1)).findProfitByPeriod(period,interval);
        assertEquals(1, actualProductProfitDto.size());
        assertNotNull(actualProductProfitDto.getFirst());
        assertEquals(productProfitDto.getPeriod(), actualProductProfitDto.getFirst().getPeriod());
        assertEquals(productProfitDto.getSum(), actualProductProfitDto.getFirst().getSum());

    }
    private Sort orderBy(String sort, Boolean ascending) {
        if (!ascending) {
            return Sort.by(Sort.Direction.DESC, sort);
        } else {
            return Sort.by(Sort.Direction.ASC, sort);
        }
    }



}
