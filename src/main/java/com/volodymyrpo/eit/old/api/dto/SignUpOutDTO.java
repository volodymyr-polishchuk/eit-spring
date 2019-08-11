package com.volodymyrpo.eit.old.api.dto;

import lombok.Data;

@Data
public class SignUpOutDTO {
    private Integer k;
    private String message;

    public SignUpOutDTO(Integer k, String message) {
        this.k = k;
        this.message = message;
    }
}
