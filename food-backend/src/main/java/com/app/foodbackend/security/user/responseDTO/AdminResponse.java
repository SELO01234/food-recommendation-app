package com.app.foodbackend.security.user.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
}
