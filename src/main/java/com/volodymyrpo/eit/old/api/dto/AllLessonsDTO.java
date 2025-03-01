package com.volodymyrpo.eit.old.api.dto;

import lombok.Data;

@Data
public class AllLessonsDTO {
    private Integer lessonID;
    private String lessonName;
    private String themeName;
    private Long timeToNowDiff;

    public AllLessonsDTO(Integer lessonID, String lessonName, String themeName, Long timeToNowDiff) {
        this.lessonID = lessonID;
        this.lessonName = lessonName;
        this.themeName = themeName;
        this.timeToNowDiff = timeToNowDiff;
    }
}
