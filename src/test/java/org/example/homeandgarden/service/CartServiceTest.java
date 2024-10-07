package org.example.homeandgarden.service;

import org.example.homeandgarden.dto.requestdto.CartItemRequestDto;
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
class CartServiceTest {

    @Mock
    private ProductRepository productRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private CartRepository cartRepositoryMock;

    @Mock
    public CartItemRepository cartItemRepositoryMock;

    @Mock
    private Mappers mappersMock;

    @InjectMocks
    private CartService cartServiceMock;

    DataNotFoundInDataBaseException dataNotFoundInDataBaseException;
    DataAlreadyExistsException dataAlreadyExistsException;

    private User user;
    private Cart cart;
    private CartItem cartItem;
    private Product product, notFoundProduct;

    private ProductResponseDto productResponseDto;
    private UserResponseDto userResponseDto;
    private CartResponseDto cartResponseDto;
    private CartItemResponseDto cartItemResponseDto;

    private CartItemRequestDto cartItemRequestDto, wrongCartItemRequestDto, existingCartItemRequestDto;

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

        cart = new Cart(1L, null, user);


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

        notFoundProduct = new Product(2L,
                "Name",
                "Description",
                new BigDecimal("10.00"),
                new BigDecimal("0.00"),
                "http://localhost/img/1.jpg",
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                new Category(1L, "Category", null),
                null,
                null,
                null);

        cartItem = new CartItem(1L, product, 2, cart);
        Set<CartItem> cartItemSet = new HashSet<>();
        cartItemSet.add(cartItem);
        cart.setCartItems(cartItemSet);
        user.setCart(cart);

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

//RequestDto
        cartItemRequestDto = CartItemRequestDto.builder()
                .productId(2L)
                .quantity(5)
                .build();

        wrongCartItemRequestDto = CartItemRequestDto.builder()
                .productId(66L)
                .quantity(5)
                .build();

        existingCartItemRequestDto = CartItemRequestDto.builder()
                .productId(1L)
                .quantity(5)
                .build();
    }

    @Test
    void getCartItems() {
        String email = "arneoswald@example.com";
        String wrongEmail = "julia.vladimirov@example.com";


        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));
        when(mappersMock.convertToCartItemResponseDto(any(CartItem.class))).thenReturn(cartItemResponseDto);
        Set<CartItemResponseDto> cartItemResponseDtoSet = new HashSet<>();
        cartItemResponseDtoSet.add(cartItemResponseDto);
        Set<CartItemResponseDto> actualCartItemSet = cartServiceMock.getCartItems(email);

        verify(userRepositoryMock, times(1)).findByEmail(email);
        verify(mappersMock, times(1)).convertToCartItemResponseDto(any(CartItem.class));

        assertFalse(actualCartItemSet.isEmpty());
        assertEquals(cartItemResponseDtoSet.size(), actualCartItemSet.size());
        assertEquals(cartItemResponseDtoSet.hashCode(), actualCartItemSet.hashCode());

        when(userRepositoryMock.findByEmail(wrongEmail)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> cartServiceMock.getCartItems(wrongEmail));
        assertEquals("User not found in database.", dataNotFoundInDataBaseException.getMessage());
    }

    @Test
    void insertCartItem() {
        String email = "arneoswald@example.com";
        String wrongEmail = "julia.vladimirov@example.com";

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepositoryMock.findById(cartItemRequestDto.getProductId())).thenReturn(Optional.of(product));
        when(cartRepositoryMock.findById(user.getCart().getCartId())).thenReturn(Optional.of(cart));

        cartServiceMock.insertCartItem(cartItemRequestDto, email);

        verify(userRepositoryMock, times(1)).findByEmail(email);
        verify(productRepositoryMock, times(1)).findById(cartItemRequestDto.getProductId());
        verify(cartRepositoryMock, times(1)).findById(user.getCart().getCartId());
        verify(cartItemRepositoryMock, times(1)).save(any(CartItem.class));

        when(userRepositoryMock.findByEmail(wrongEmail)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> cartServiceMock.insertCartItem(cartItemRequestDto, wrongEmail));
        assertEquals("User not found in database.", dataNotFoundInDataBaseException.getMessage());


        when(productRepositoryMock.findById(wrongCartItemRequestDto.getProductId())).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> cartServiceMock.insertCartItem(wrongCartItemRequestDto, email));
        assertEquals("Product not found in database.", dataNotFoundInDataBaseException.getMessage());


        when(productRepositoryMock.findById(existingCartItemRequestDto.getProductId())).thenReturn(Optional.of(product));
        dataAlreadyExistsException = assertThrows(DataAlreadyExistsException.class,
                () -> cartServiceMock.insertCartItem(existingCartItemRequestDto, email));
        assertEquals("This product is already in cart.", dataAlreadyExistsException.getMessage());

    }

    @Test
    void insertCartItemWhenNoCart() {
        String email = "arneoswald@example.com";

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepositoryMock.findById(cartItemRequestDto.getProductId())).thenReturn(Optional.of(product));
        when(cartRepositoryMock.findById(user.getCart().getCartId())).thenReturn(Optional.empty());
        when(cartRepositoryMock.save(any(Cart.class))).thenReturn(cart);
        when(cartItemRepositoryMock.save(any(CartItem.class))).thenReturn(cartItem);


        cartServiceMock.insertCartItem(cartItemRequestDto, email);

        verify(userRepositoryMock, times(1)).findByEmail(email);
        verify(productRepositoryMock, times(1)).findById(cartItemRequestDto.getProductId());
        verify(cartRepositoryMock, times(1)).findById(user.getCart().getCartId());
        verify(cartRepositoryMock, times(2)).save(any(Cart.class));
        verify(cartItemRepositoryMock, times(1)).save(any(CartItem.class));
    }

    @Test
    void insertCartItemWhenNoCartItemSet() {
        String email = "arneoswald@example.com";

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepositoryMock.findById(cartItemRequestDto.getProductId())).thenReturn(Optional.of(product));
        when(cartRepositoryMock.findById(user.getCart().getCartId())).thenReturn(Optional.of(cart));
        cart.setCartItems(null);
        when(cartItemRepositoryMock.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepositoryMock.save(any(Cart.class))).thenReturn(cart);




        cartServiceMock.insertCartItem(cartItemRequestDto, email);

        verify(userRepositoryMock, times(1)).findByEmail(email);
        verify(productRepositoryMock, times(1)).findById(cartItemRequestDto.getProductId());
        verify(cartRepositoryMock, times(1)).findById(user.getCart().getCartId());
        verify(cartRepositoryMock, times(1)).save(any(Cart.class));
        verify(cartItemRepositoryMock, times(2)).save(any(CartItem.class));
    }

    @Test
    void deleteCartItemByProductId() {

        String email = "arneoswald@example.com";
        String wrongEmail = "julia.vladimirov@example.com";

        Long productId = 1L;
        Long wrongProductId = 75L;
        Long notFoundProductId = 2L;

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepositoryMock.findById(productId)).thenReturn(Optional.of(product));

        cartServiceMock.deleteCartItemByProductId(email, productId);

        verify(userRepositoryMock, times(1)).findByEmail(email);
        verify(productRepositoryMock, times(1)).findById(productId);
        verify(cartItemRepositoryMock, times(1)).deleteById(user.getCart().getCartItems().iterator().next().getCartItemId());


        when(userRepositoryMock.findByEmail(wrongEmail)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> cartServiceMock.deleteCartItemByProductId(wrongEmail, productId));
        assertEquals("User not found in database.", dataNotFoundInDataBaseException.getMessage());

        when(productRepositoryMock.findById(wrongProductId)).thenReturn(Optional.empty());
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> cartServiceMock.deleteCartItemByProductId(email, wrongProductId));
        assertEquals("Product not found in database.", dataNotFoundInDataBaseException.getMessage());

        when(productRepositoryMock.findById(notFoundProductId)).thenReturn(Optional.of(notFoundProduct));
        dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> cartServiceMock.deleteCartItemByProductId(email, notFoundProductId));
        assertEquals("No such product found in your cart.", dataNotFoundInDataBaseException.getMessage());
    }
}