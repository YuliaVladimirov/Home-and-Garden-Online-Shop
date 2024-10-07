package org.example.homeandgarden.service;

import org.example.homeandgarden.config.MapperUtil;
import org.example.homeandgarden.dto.requestdto.FavoriteRequestDto;
import org.example.homeandgarden.dto.responsedto.FavoriteResponseDto;
import org.example.homeandgarden.entity.*;
import org.example.homeandgarden.exception.DataAlreadyExistsException;
import org.example.homeandgarden.exception.DataNotFoundInDataBaseException;
import org.example.homeandgarden.mapper.Mappers;
import org.example.homeandgarden.repository.FavoriteRepository;
import org.example.homeandgarden.repository.ProductRepository;
import org.example.homeandgarden.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final Mappers mappers;

    public Set<FavoriteResponseDto> getFavorites(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            Set<Favorite> favoritesList = user.getFavorites();
            return MapperUtil.convertSet(favoritesList, mappers::convertToFavoriteResponseDto);
        } else {
            throw new DataNotFoundInDataBaseException("User not found in database.");
        }
    }

    public void insertFavorite(FavoriteRequestDto favoriteRequestDto, String email) {
        Favorite favorite = new Favorite();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            Product product = productRepository.findById(favoriteRequestDto.getProductId()).orElse(null);
            if (product != null) {
                Set<Favorite> favoriteSet = user.getFavorites();
                for (Favorite item : favoriteSet) {
                    if (item.getProduct().getProductId().equals(favoriteRequestDto.getProductId())) {
                        throw new DataAlreadyExistsException("This product is already in favorites.");
                    }
                }
                favorite.setProduct(product);
                favorite.setUser(user);
                favoriteRepository.save(favorite);
                favoriteSet.add(favorite);

            } else {
                throw new DataNotFoundInDataBaseException("Product not found in database.");
            }
        } else {
            throw new DataNotFoundInDataBaseException("User not found in database.");
        }

    }


    public void deleteFavoriteByProductId(String email, Long productId) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                Set<Favorite> favoritesSet = user.getFavorites();
                for (Favorite item : favoritesSet) {
                    if (item.getProduct().getProductId().equals(productId)) {
                        favoriteRepository.deleteById(item.getFavoriteId());
                        return;
                    }
                }
                throw new DataNotFoundInDataBaseException("Product not found in Favorites.");
            } else {
                throw new DataNotFoundInDataBaseException("Product not found in database.");
            }
        } else {
            throw new DataNotFoundInDataBaseException("User not found in database.");
        }

    }
}
