package com.volodymyrpo.eit.old.api.dto;

import lombok.Data;

@Data
public class AllThemeDTO {
    String k;
    String name;

    public AllThemeDTO(Integer k, String name) {
        this.k = k.toString();
        this.name = name;
    }
}
