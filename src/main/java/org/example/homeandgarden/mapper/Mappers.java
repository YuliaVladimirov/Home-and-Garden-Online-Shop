package org.example.homeandgarden.mapper;

import org.example.homeandgarden.dto.querydto.ProductCountDto;
import org.example.homeandgarden.dto.querydto.ProductPendingDto;
import org.example.homeandgarden.dto.querydto.ProductProfitDto;
import org.example.homeandgarden.dto.requestdto.*;
import org.example.homeandgarden.dto.responsedto.*;
import org.example.homeandgarden.entity.*;
import org.example.homeandgarden.entity.query.ProductCountInterface;
import org.example.homeandgarden.entity.query.ProductPendingInterface;
import org.example.homeandgarden.entity.query.ProductProfitInterface;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mappers {

    private final ModelMapper modelMapper;

    public UserResponseDto convertToUserResponseDto(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }

    public User convertToUser(UserRequestDto userRequestDto) {
        return modelMapper.map(userRequestDto, User.class);
    }

    public FavoriteResponseDto convertToFavoriteResponseDto(Favorite favorite) {
        FavoriteResponseDto favoriteResponseDto = modelMapper.map(favorite, FavoriteResponseDto.class);
        modelMapper.typeMap(Favorite.class, FavoriteResponseDto.class)
        .addMappings(mapper -> mapper.skip(FavoriteResponseDto::setUserResponseDto));
        favoriteResponseDto.setProductResponseDto(convertToProductResponseDto(favorite.getProduct()));
        return favoriteResponseDto;
    }

    public CartItemResponseDto convertToCartItemResponseDto(CartItem cartItem) {
        modelMapper.typeMap(CartItem.class, CartItemResponseDto.class)
                .addMappings(mapper -> mapper.skip(CartItemResponseDto::setCartResponseDto));
        CartItemResponseDto cartItemResponseDto = modelMapper.map(cartItem, CartItemResponseDto.class);
        cartItemResponseDto.setProductResponseDto(convertToProductResponseDto(cartItem.getProduct()));
        return cartItemResponseDto;
    }

    public OrderResponseDto convertToOrderResponseDto(Order order) {
           modelMapper.typeMap(Order.class, OrderResponseDto.class)
            .addMappings(mapper -> mapper.skip(OrderResponseDto::setUserResponseDto));
        return modelMapper.map(order, OrderResponseDto.class);

    }

    public OrderItemResponseDto convertToOrderItemResponseDto(OrderItem orderItem) {
        modelMapper.typeMap(OrderItem.class, OrderItemResponseDto.class)
                .addMappings(mapper -> mapper.skip(OrderItemResponseDto::setOrderResponseDto));
        OrderItemResponseDto orderItemResponseDto = modelMapper.map(orderItem, OrderItemResponseDto.class);
        orderItemResponseDto.setProductResponseDto(convertToProductResponseDto(orderItem.getProduct()));
        return orderItemResponseDto;
    }

    public Product convertToProduct(ProductRequestDto productRequestDto) {
        return modelMapper.map(productRequestDto, Product.class);
    }

    public ProductResponseDto convertToProductResponseDto(Product product) {
        modelMapper.typeMap(Product.class, ProductResponseDto.class)
                .addMappings(mapper -> mapper.skip(ProductResponseDto::setCategoryResponseDto));
        return modelMapper.map(product, ProductResponseDto.class);
    }

    public CategoryResponseDto convertToCategoryResponseDto(Category category) {
        return modelMapper.map(category, CategoryResponseDto.class);
    }

    public Category convertToCategory(CategoryRequestDto categoryRequestDto) {
        return modelMapper.map(categoryRequestDto, Category.class);
    }

    public ProductCountDto convertToProductCountDto(ProductCountInterface productCountInterface) {
        return modelMapper.map(productCountInterface, ProductCountDto.class);
    }

    public ProductPendingDto convertToProductPendingDto(ProductPendingInterface productPendingInterface) {
        return modelMapper.map(productPendingInterface, ProductPendingDto.class);
    }

    public ProductProfitDto convertToProductProfitDto(ProductProfitInterface productProfitInterface) {
        return modelMapper.map(productProfitInterface, ProductProfitDto.class);
    }
}