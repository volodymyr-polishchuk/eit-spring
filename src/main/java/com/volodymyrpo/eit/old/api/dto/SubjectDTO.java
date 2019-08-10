package com.volodymyrpo.eit.old.api.dto;

import lombok.Data;

@Data
public class SubjectDTO {
    Integer k;
    String name;

    public SubjectDTO(Integer k, String name) {
        this.k = k;
        this.name = name;
    }
}
