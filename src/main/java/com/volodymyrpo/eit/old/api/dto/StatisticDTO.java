package com.volodymyrpo.eit.old.api.dto;

import lombok.Data;

@Data
public class StatisticDTO {
    private String subject_name;
    private Integer seconds;

    public StatisticDTO(String subjectName, Integer seconds) {
        this.subject_name = subjectName;
        this.seconds = seconds;
    }
}
