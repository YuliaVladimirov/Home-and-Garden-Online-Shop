package org.example.homeandgarden.dto.querydto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCountDto {
    private Long productId;
    private String name;
    private String status;
    private Integer count;
    private BigDecimal sum;

}
