package com.volodymyrpo.eit.lessons;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("lessons")
public class LessonResource {

    private final LessonRepository lessonRepository;

    public LessonResource(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @GetMapping()
    public ResponseEntity<List<Lesson>> getAll() {
        List<Lesson> lessons = this.lessonRepository.findAll();
        return ResponseEntity.ok(lessons);
    }

}
