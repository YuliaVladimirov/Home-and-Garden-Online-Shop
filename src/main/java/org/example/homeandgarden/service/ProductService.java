package org.example.homeandgarden.service;

import org.example.homeandgarden.config.MapperUtil;
import org.example.homeandgarden.dto.querydto.ProductCountDto;
import org.example.homeandgarden.dto.querydto.ProductPendingDto;
import org.example.homeandgarden.dto.querydto.ProductProfitDto;
import org.example.homeandgarden.dto.requestdto.ProductRequestDto;
import org.example.homeandgarden.dto.responsedto.ProductResponseDto;
import org.example.homeandgarden.entity.Category;
import org.example.homeandgarden.entity.Product;
import org.example.homeandgarden.exception.DataNotFoundInDataBaseException;
import org.example.homeandgarden.mapper.Mappers;
import org.example.homeandgarden.repository.CategoryRepository;
import org.example.homeandgarden.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final Mappers mappers;


    public ProductResponseDto getProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            return mappers.convertToProductResponseDto(product);

        } else {
            throw new DataNotFoundInDataBaseException("Product not found in database.");
        }
    }


    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            productRepository.deleteById(product.getProductId());
        } else {
            throw new DataNotFoundInDataBaseException("Product not found in database.");
        }
    }


    public void insertProduct(ProductRequestDto productRequestDto) {
        Category category = categoryRepository.findCategoryByName(productRequestDto.getCategory());
        if (category != null) {
            Product productToInsert = mappers.convertToProduct(productRequestDto);
            productToInsert.setProductId(0L);
            productToInsert.setCategory(category);
            productToInsert.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            productRepository.save(productToInsert);
        } else {
            throw new DataNotFoundInDataBaseException("Category not found in database.");
        }
    }


    public void updateProduct(ProductRequestDto productRequestDto, Long id) {
        Category category = categoryRepository.findCategoryByName(productRequestDto.getCategory());
        if (category != null) {
            Product productToUpdate = productRepository.findById(id).orElse(null);
            if (productToUpdate != null) {
                productToUpdate.setName(productRequestDto.getName());
                productToUpdate.setDescription(productRequestDto.getDescription());
                productToUpdate.setPrice(productRequestDto.getPrice());
                productToUpdate.setImageUrl(productRequestDto.getImageUrl());
                productToUpdate.setCategory(category);
                productToUpdate.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                productRepository.save(productToUpdate);
            } else {
                throw new DataNotFoundInDataBaseException("Product not found in database.");
            }
        } else {
            throw new DataNotFoundInDataBaseException("Category not found in database.");
        }

    }


    public void setDiscountPrice(Long id, BigDecimal discountPrice) {
        Product productToUpdate = productRepository.findById(id).orElse(null);
        if (productToUpdate != null) {
            productToUpdate.setDiscountPrice(discountPrice);
            productRepository.save(productToUpdate);
        } else {
            throw new DataNotFoundInDataBaseException("Product not found in database.");
        }
    }

    public ProductResponseDto getMaxDiscountProduct() {
        List<Product> maxDiscountProductList = productRepository.getMaxDiscountProduct();
        if (maxDiscountProductList.size() > 1) {
            Random random = new Random();
            int randomNumber = random.nextInt(maxDiscountProductList.size());
            return mappers.convertToProductResponseDto(maxDiscountProductList.get(randomNumber));
        } else {
            return mappers.convertToProductResponseDto(maxDiscountProductList.getFirst());
        }
    }
    public List<ProductCountDto> getTop10Products(String status) {

        return MapperUtil.convertList(productRepository.findTop10Products(status),mappers::convertToProductCountDto);
    }


    public List<ProductResponseDto> getProductsByFilter(Long category, BigDecimal minPrice, BigDecimal maxPrice, Boolean hasDiscount, String sort) {
        boolean ascending = true;
        Sort sortObject = orderBy("name", true);
        boolean hasCategory = false;

        if (category != null) { hasCategory = true; }
        if (minPrice == null) { minPrice =BigDecimal.valueOf( 0.00); }
        if (maxPrice == null) { maxPrice =BigDecimal.valueOf( Double.MAX_VALUE); }
        if (sort != null) {
            String[] sortArray = sort.split(",");
            if (sortArray[1].equals("desc")) {
                ascending = false;
            }
            sortObject = orderBy(sortArray[0], ascending);
        }
        return MapperUtil.convertList(productRepository.findProductsByFilter(hasCategory, category, minPrice, maxPrice, hasDiscount, sortObject), mappers::convertToProductResponseDto);
    }


    public List<ProductPendingDto> findProductPending(Integer day) {
        return MapperUtil.convertList(productRepository.findProductPending(day),mappers::convertToProductPendingDto);
    }


    public List<ProductProfitDto> findProductProfit(String period, Integer value) {
        return MapperUtil.convertList(productRepository.findProfitByPeriod(period, value),mappers::convertToProductProfitDto);
    }


    private Sort orderBy(String sort, Boolean ascending) {
        if (!ascending) {
            return Sort.by(Sort.Direction.DESC, sort);
        } else {
            return Sort.by(Sort.Direction.ASC, sort);
        }
    }

}