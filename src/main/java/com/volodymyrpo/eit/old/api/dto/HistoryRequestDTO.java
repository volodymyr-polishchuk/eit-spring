package com.volodymyrpo.eit.old.api.dto;

import lombok.Data;

@Data
public class HistoryRequestDTO {
    private Integer subject;
    private Boolean group;
    private String from_date;
    private String to_date;
}
