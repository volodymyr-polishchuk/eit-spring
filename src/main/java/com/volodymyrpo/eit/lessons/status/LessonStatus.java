package com.volodymyrpo.eit.lessons.status;

import lombok.Getter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Getter
@Entity
public enum LessonStatus {
    FINISHED(0, "FINISHED"),
    ACTIVE(1, "ACTIVE"),
    CANCELED(2, "CANCELED");

    @Id
    private Integer id;
    private String name;

    LessonStatus() {
    }

    LessonStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
