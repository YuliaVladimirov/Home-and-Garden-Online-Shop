package org.example.homeandgarden.dto.requestdto;

import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {

    @JsonProperty("items")
    @NotEmpty(message = "Items list cannot be empty")
    private Set<@Valid OrderItemRequestDto> orderItemsSet;


    @Size(min = 1, max = 255, message = "Delivery address must be less than or equal to 255 characters")
    private String deliveryAddress;

    @Pattern(regexp = "^(COURIER_DELIVERY|CUSTOMER_PICKUP)$", message = "Invalid Delivery method: Must be one of: COURIER_DELIVERY or CUSTOMER_PICKUP")
    private String deliveryMethod;
}

