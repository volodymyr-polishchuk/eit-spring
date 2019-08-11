package com.volodymyrpo.eit.old.api.dto;

import lombok.Data;

@Data
public class SubjectDTO {
    String k;
    String name;

    public SubjectDTO(Integer k, String name) {
        this.k = k.toString();
        this.name = name;
    }
}
