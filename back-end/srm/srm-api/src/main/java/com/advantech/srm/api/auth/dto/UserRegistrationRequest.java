package com.advantech.srm.api.auth.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String email;
    private String password;
    private String name;
}
