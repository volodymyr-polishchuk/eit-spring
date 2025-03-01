package com.volodymyrpo.eit.old.api.dto;

import lombok.Data;

@Data
public class UserInfoDTO {
    private String login;
    private String name;
    private String description;

    public UserInfoDTO(String login, String name, String description) {
        this.login = login;
        this.name = name;
        this.description = description;
    }
}
