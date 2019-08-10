package com.volodymyrpo.eit.security.dto;

import lombok.Data;

@Data
public class JwtAuthenticationRequest {

    private String username;
    private String password;

}
