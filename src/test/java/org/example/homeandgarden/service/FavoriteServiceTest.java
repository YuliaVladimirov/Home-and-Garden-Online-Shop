package org.example.homeandgarden.service;

import org.example.homeandgarden.dto.requestdto.FavoriteRequestDto;
import org.example.homeandgarden.dto.responsedto.*;
import org.example.homeandgarden.entity.*;
import org.example.homeandgarden.entity.enums.Role;
import org.example.homeandgarden.exception.DataAlreadyExistsException;
import org.example.homeandgarden.exception.DataNotFoundInDataBaseException;
import org.example.homeandgarden.mapper.Mappers;
import org.example.homeandgarden.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private ProductRepository productRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private FavoriteRepository favoriteRepositoryMock;

    @InjectMocks
    private FavoriteService favoriteServiceMock;

    @Mock
    private Mappers mappersMock;

    DataNotFoundInDataBaseException dataNotFoundInDataBaseException;
    DataAlreadyExistsException dataAlreadyExistsException;

    private User user;
    private Product product, newProduct;
    private Favorite favorite;

    private UserResponseDto userResponseDto;
    private ProductResponseDto productResponseDto;
    private FavoriteResponseDto favoriteResponseDto;

    private FavoriteRequestDto favoriteRequestDto, wrongFavoriteRequestDto, existingFavoriteRequestDto;

    private Set<Favorite> favoriteSet = new HashSet<>();
    private Set<FavoriteResponseDto> favoriteResponseDtoSet = new HashSet<>();


    @BeforeEach
    void setUp() {

//Entity
        user = new User(1L,
                "Arne Oswald",
                "arneoswald@example.com",
                "+496151226",
                "Pass1$trong",
                Role.CLIENT,
                null,
                null,
                null,
                null);


        product = new Product(1L,
                "Name",
                "Description",
                new BigDecimal("100.00"),
                new BigDecimal("0.00"),
                "http://localhost/img/1.jpg",
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                new Category(1L, "Category", null),
                null,
                null,
                null);


        favorite = new Favorite(1L,
                user,
                product);

        favoriteSet.add(favorite);
        user.setFavorites(favoriteSet);

        newProduct = new Product(2L,
                "Name2",
                "Description2",
                new BigDecimal("100.00"),
                new BigDecimal("0.00"),
                "http://localhost/img/1.jpg",
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                new Category(1L, "Category", null),
                null,
                null,
                null);

//ResponseDto
        userResponseDto = UserResponseDto.builder()
                .userId(1L)
                .name("Arne Oswald")
                .email("arneoswald@example.com")
                .phone("+496151226")
                .passwordHash("Pass1$trong")
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
                .productId(2L)
                .build();

        wrongFavoriteRequestDto = FavoriteRequestDto.builder()
                .productId(58L)
                .build();

        existingFavoriteRequestDto = FavoriteRequestDto.builder()
                .productId(1L)
                .build();
    }

    @Test
    void getFavorite() {

        String email = "arneoswald@example.com";
        String wrongEmail = "julia.vladimirov@example.com";

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));
        when(mappersMock.convertToFavoriteResponseDto(any(Favorite.class))).thenReturn(favoriteResponseDto);

        Set<FavoriteResponseDto> actualFavoriteResponseDtoSet = favoriteServiceMock.getFavorites(email);

        verify(userRepositoryMock, times(1)).findByEmail(email);
        verify(mappersMock, times(1)).convertToFavoriteResponseDto(any(Favorite.class));

        assertFalse(actualFavoriteResponseDtoSet.isEmpty());
        assertEquals(favoriteResponseDtoSet.size(), actualFavoriteResponseDtoSet.size());
        assertEquals(favoriteResponseDtoSet.hashCode(), actualFavoriteResponseDtoSet.hashCode());

        when(userRepositoryMock.findByEmail(wrongEmail)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> favoriteServiceMock.getFavorites(wrongEmail));
        assertEquals("User not found in database.", dataNotFoundInDataBaseException.getMessage());
    }

    @Test
    void insertFavorite() {

        String email = "arneoswald@example.com";
        String wrongEmail = "julia.vladimirov@example.com";

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepositoryMock.findById(favoriteRequestDto.getProductId())).thenReturn(Optional.of(newProduct));

        favoriteServiceMock.insertFavorite(favoriteRequestDto, email);

        verify(favoriteRepositoryMock, times(1)).save(any(Favorite.class));

        when(userRepositoryMock.findByEmail(wrongEmail)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> favoriteServiceMock.insertFavorite(favoriteRequestDto, wrongEmail));
        assertEquals("User not found in database.", dataNotFoundInDataBaseException.getMessage());

        when(productRepositoryMock.findById(wrongFavoriteRequestDto.getProductId())).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> favoriteServiceMock.insertFavorite(wrongFavoriteRequestDto, email));
        assertEquals("Product not found in database.", dataNotFoundInDataBaseException.getMessage());

        when(productRepositoryMock.findById(existingFavoriteRequestDto.getProductId())).thenReturn(Optional.of(product));
        dataAlreadyExistsException = assertThrows(DataAlreadyExistsException.class,
                () -> favoriteServiceMock.insertFavorite(existingFavoriteRequestDto, email));
        assertEquals("This product is already in favorites.", dataAlreadyExistsException.getMessage());
    }

    @Test
    void deleteFavoriteByProductId() {

        String email = "arneoswald@example.com";
        String wrongEmail = "julia.vladimirov@example.com";

        Long productId = 1L;
        Long wrongProductId = 58L;


        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepositoryMock.findById(productId)).thenReturn(Optional.of(product));

        favoriteServiceMock.deleteFavoriteByProductId(email, productId);

        verify(favoriteRepositoryMock, times(1)).deleteById(user.getFavorites().iterator().next().getFavoriteId());


        when(userRepositoryMock.findByEmail(wrongEmail)).thenReturn(Optional.empty());
        when(productRepositoryMock.findById(wrongProductId)).thenReturn(Optional.empty());


        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> favoriteServiceMock.deleteFavoriteByProductId(email, wrongProductId));
        assertEquals("Product not found in database.", dataNotFoundInDataBaseException.getMessage());

        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> favoriteServiceMock.deleteFavoriteByProductId(wrongEmail, productId));
        assertEquals("User not found in database.", dataNotFoundInDataBaseException.getMessage());

    }
}