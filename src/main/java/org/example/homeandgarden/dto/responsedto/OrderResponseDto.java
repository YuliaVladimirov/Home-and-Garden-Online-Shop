package org.example.homeandgarden.dto.responsedto;

import org.example.homeandgarden.entity.enums.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private Long orderId;
    private Timestamp createdAt;
    private String deliveryAddress;
    private String contactPhone;
    private DeliveryMethod deliveryMethod;
    private Status status;
    private Timestamp updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("user")
    private UserResponseDto userResponseDto;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("items")
    private Set<OrderItemResponseDto> orderItemsSet;
}

