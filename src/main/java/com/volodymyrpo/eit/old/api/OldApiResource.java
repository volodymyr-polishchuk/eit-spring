package com.volodymyrpo.eit.old.api;

import com.volodymyrpo.eit.lessons.Lesson;
import com.volodymyrpo.eit.lessons.LessonRepository;
import com.volodymyrpo.eit.lessons.status.LessonStatus;
import com.volodymyrpo.eit.old.api.dto.*;
import com.volodymyrpo.eit.security.exception.NotFoundException;
import com.volodymyrpo.eit.student.Student;
import com.volodymyrpo.eit.subject.Subject;
import com.volodymyrpo.eit.subject.SubjectRepository;
import com.volodymyrpo.eit.topic.Topic;
import com.volodymyrpo.eit.topic.TopicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1")
@CrossOrigin
public class OldApiResource {

    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;

    public OldApiResource(LessonRepository lessonRepository, SubjectRepository subjectRepository, TopicRepository topicRepository) {
        this.lessonRepository = lessonRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
    }

    @GetMapping("all_lessons.php")
    public List<AllLessonsDTO> getAllLessons() {
        List<Lesson> lessons = lessonRepository.findByStudentAndLessonStatus(getStudent(), LessonStatus.ACTIVE);

        return lessons.stream()
                .map(lesson -> {
                    long now = Instant.now().toEpochMilli();
                    long dateStartMilli = lesson.getDateStart().toInstant(ZoneOffset.UTC).toEpochMilli();
                    long diffInSeconds = (now - dateStartMilli) / 1000;
                    return new AllLessonsDTO(
                            lesson.getId(),
                            lesson.getSubject().getName(),
                            lesson.getTopic().getName(),
                            diffInSeconds);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("all_subject.php")
    public List<SubjectDTO> getAllSubject() {
        List<Subject> subjects = subjectRepository.findByStudent(getStudent());

        return subjects.stream()
                .map(subject -> new SubjectDTO(subject.getId(), subject.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "all_themes.php", params = {"subject_code"})
    public List<AllThemeDTO> getAllTheme(@RequestParam("subject_code") Integer subjectId) {
        List<Topic> topics = topicRepository.findBySubjectId(subjectId);

        return topics.stream()
                .map(topic -> new AllThemeDTO(topic.getId(), topic.getName()))
                .collect(Collectors.toList());
    }

    @PostMapping("cancel_lesson.php")
    public Integer cancelLesson(@RequestBody CancelLessonDTO dto) throws NotFoundException {
        Lesson lesson = lessonRepository.findById(dto.getLesson_id())
                .orElseThrow(() -> new NotFoundException(""));
        lesson.setLessonStatus(LessonStatus.CANCELED);
        lesson.setDateEnd(LocalDateTime.now());
        lessonRepository.save(lesson);
        return lesson.getId();
    }

    @PostMapping("create_subject.php")
    public ResponseEntity createSubject(@RequestBody CreateSubjectDTO dto) {
        Optional<Subject> subjectOptional = Optional.ofNullable(subjectRepository.findByName(dto.getSubject_name()));
        if (subjectOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new MessageDTO("already exist"));
        }
        Subject subject = new Subject();
        subject.setName(dto.getSubject_name());
        subject.setStudent(getStudent());
        subjectRepository.save(subject);
        return ResponseEntity.ok(new SubjectDTO(subject.getId(), subject.getName()));
    }

    private Student getStudent() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (Student) principal;
    }

}
