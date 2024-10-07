package org.example.homeandgarden.dto.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @Size(min = 2, max = 50, message = "Invalid name: Must be of 2 - 50 characters")
    private String name;


    @Size(min = 2, max = 255, message = "Invalid description: Must be of 2 - 255 characters")
    private String description;


    @DecimalMin(value = "0.00")
    @Digits(integer=6, fraction=2)
    private BigDecimal price;


    @Pattern(regexp = "^https?://([-a-z0-9]{2,256}\\.){1,20}[a-z]{2,4}/[-a-zA-Z0-9_.#?&=%/]*$", message = "Invalid URL")
    @JsonProperty("image")
    private String imageUrl;


    @Size(min = 2, max = 50, message = "Invalid category: Must be of 2 - 50 characters")
    private String category;
}
