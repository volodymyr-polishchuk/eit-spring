package com.volodymyrpo.eit.old.api.dto;

import lombok.Data;

@Data
public class AllLessonsDTO {
    private String lessonID;
    private String lessonName;
    private String themeName;
    private Long timeToNowDiff;

    public AllLessonsDTO(Integer lessonID, String lessonName, String themeName, Long timeToNowDiff) {
        this.lessonID = lessonID.toString();
        this.lessonName = lessonName;
        this.themeName = themeName;
        this.timeToNowDiff = timeToNowDiff;
    }
}
