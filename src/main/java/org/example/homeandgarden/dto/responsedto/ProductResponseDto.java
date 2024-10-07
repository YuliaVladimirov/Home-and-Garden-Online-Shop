package org.example.homeandgarden.dto.responsedto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal discountPrice;

    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String imageUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("category")
    private CategoryResponseDto categoryResponseDto;
}
