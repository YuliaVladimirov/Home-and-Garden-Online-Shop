package org.example.homeandgarden.dto.responsedto;

import org.example.homeandgarden.entity.enums.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private Long userId;
    private String name;
    private String phone;
    private String email;
    private String passwordHash;
    private Role role;

}


