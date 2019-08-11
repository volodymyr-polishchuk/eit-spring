package com.volodymyrpo.eit.old.api.dto;

import lombok.Data;

@Data
public class DeleteMessageDTO {
    private Integer deleted;
    private String message;

    public DeleteMessageDTO(Integer deleted, String message) {
        this.deleted = deleted;
        this.message = message;
    }
}
