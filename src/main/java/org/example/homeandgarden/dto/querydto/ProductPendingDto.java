package org.example.homeandgarden.dto.querydto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPendingDto {

    private Long productId;
    private String name;
    private Integer count;
    private String status;
}
