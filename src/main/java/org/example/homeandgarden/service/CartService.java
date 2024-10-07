package org.example.homeandgarden.service;

import org.example.homeandgarden.config.MapperUtil;
import org.example.homeandgarden.dto.requestdto.CartItemRequestDto;
import org.example.homeandgarden.dto.responsedto.CartItemResponseDto;
import org.example.homeandgarden.entity.*;
import org.example.homeandgarden.exception.DataAlreadyExistsException;
import org.example.homeandgarden.exception.DataNotFoundInDataBaseException;
import org.example.homeandgarden.mapper.Mappers;
import org.example.homeandgarden.repository.CartItemRepository;
import org.example.homeandgarden.repository.CartRepository;
import org.example.homeandgarden.repository.ProductRepository;
import org.example.homeandgarden.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private final Mappers mappers;


    public Set<CartItemResponseDto> getCartItems(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            Set<CartItem> cartItemsSet = user.getCart().getCartItems();
            return MapperUtil.convertSet(cartItemsSet, mappers::convertToCartItemResponseDto);
        } else {
            throw new DataNotFoundInDataBaseException("User not found in database.");
        }
    }

    @Transactional
    public void insertCartItem(CartItemRequestDto cartItemRequestDto, String email) {
        CartItem cartItemToInsert = new CartItem();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            Product product = productRepository.findById(cartItemRequestDto.getProductId()).orElse(null);
            if (product != null) {
                Cart cart = cartRepository.findById(user.getCart().getCartId()).orElse(null);
                if (cart != null) {
                    Set<CartItem> cartItemSet = cart.getCartItems();
                    if(cartItemSet == null) {
                        cartItemToInsert.setCart(cart);
                        cartItemToInsert.setCartItemId(0L);
                        cartItemToInsert.setProduct(product);
                        cartItemToInsert.setQuantity(cartItemRequestDto.getQuantity());
                        cartItemRepository.save(cartItemToInsert);

                        Set<CartItem> cartItemToInsertSet = new HashSet<>();
                        cartItemToInsertSet.add(cartItemToInsert);
                        cart.setCartItems(cartItemToInsertSet);
                        cartRepository.save(cart);
                    } else {
                        for (CartItem item : cartItemSet) {
                            if (item.getProduct().getProductId().equals(cartItemRequestDto.getProductId())) {
                                throw new DataAlreadyExistsException("This product is already in cart.");
                            }
                        }
                    }
                    cartItemToInsert.setCart(cart);
                    cartItemToInsert.setCartItemId(0L);
                    cartItemToInsert.setProduct(product);
                    cartItemToInsert.setQuantity(cartItemRequestDto.getQuantity());
                    cartItemRepository.save(cartItemToInsert);
                }
                else {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    Cart savedCart = cartRepository.save(newCart);
                    cartItemToInsert.setCart(savedCart);
                    cartItemToInsert.setCartItemId(0L);
                    cartItemToInsert.setProduct(product);
                    cartItemToInsert.setQuantity(cartItemRequestDto.getQuantity());
                    CartItem savedCartItem = cartItemRepository.save(cartItemToInsert);
                    Set<CartItem> newCartItemSet = new HashSet<>();
                    newCartItemSet.add(savedCartItem);
                    savedCart.setCartItems(newCartItemSet);
                    cartRepository.save(savedCart);
                }

            } else {
                throw new DataNotFoundInDataBaseException("Product not found in database.");
            }
        } else {
            throw new DataNotFoundInDataBaseException("User not found in database.");
        }
    }

    public void deleteCartItemByProductId(String email, Long productId) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                Set<CartItem> cartItemSet = user.getCart().getCartItems();
                Set<CartItem> foundProductsSet = new HashSet<>();
                for (CartItem item : cartItemSet) {
                    if (item.getProduct().getProductId().equals(productId)) {
                        foundProductsSet.add(item);
                        cartItemRepository.deleteById(item.getCartItemId());
                    }
                    if(foundProductsSet.isEmpty()){
                        throw new DataNotFoundInDataBaseException("No such product found in your cart.");
                    }
                }
            } else {
                throw new DataNotFoundInDataBaseException("Product not found in database.");
            }

        } else {
            throw new DataNotFoundInDataBaseException("User not found in database.");
        }
    }
}
