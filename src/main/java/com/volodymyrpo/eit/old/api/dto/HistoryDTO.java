package com.volodymyrpo.eit.old.api.dto;

import lombok.Data;

@Data
public class HistoryDTO {
    private String subjectName;
    private String themeName;
    private String dateStart;
    private String dateEnd;
    private Integer time;
}
