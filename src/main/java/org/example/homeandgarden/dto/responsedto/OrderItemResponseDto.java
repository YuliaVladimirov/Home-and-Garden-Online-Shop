package org.example.homeandgarden.dto.responsedto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDto {

    private Long orderItemId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("order")
    private OrderResponseDto orderResponseDto;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("product")
    private ProductResponseDto productResponseDto;

    private BigDecimal priceAtPurchase;
    private Integer quantity;
}
