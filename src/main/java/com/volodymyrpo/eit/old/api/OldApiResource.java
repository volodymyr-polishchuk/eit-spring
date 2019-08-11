package com.volodymyrpo.eit.old.api;

import com.volodymyrpo.eit.lessons.Lesson;
import com.volodymyrpo.eit.lessons.LessonRepository;
import com.volodymyrpo.eit.lessons.status.LessonStatus;
import com.volodymyrpo.eit.old.api.dto.*;
import com.volodymyrpo.eit.security.exception.NotFoundException;
import com.volodymyrpo.eit.student.Student;
import com.volodymyrpo.eit.student.StudentRepository;
import com.volodymyrpo.eit.subject.Subject;
import com.volodymyrpo.eit.subject.SubjectRepository;
import com.volodymyrpo.eit.topic.Topic;
import com.volodymyrpo.eit.topic.TopicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
    private final StudentRepository studentRepository;
    private final AuthenticationManager authenticationManager;

    public OldApiResource(LessonRepository lessonRepository, SubjectRepository subjectRepository, TopicRepository topicRepository, StudentRepository studentRepository, AuthenticationManager authenticationManager) {
        this.lessonRepository = lessonRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.studentRepository = studentRepository;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("all_lessons.php")
    public List<AllLessonsDTO> getAllLessons() {
        List<Lesson> lessons = lessonRepository.findByStudentAndLessonStatus(getStudent(), LessonStatus.ACTIVE);

        return lessons.stream()
                .map(lesson -> {
                    long now = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
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
        List<Subject> subjects = subjectRepository.findByStudentAndActive(getStudent(), true);

        return subjects.stream()
                .map(subject -> new SubjectDTO(subject.getId(), subject.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "all_themes.php", params = {"subject_code"})
    public List<AllThemeDTO> getAllTheme(@RequestParam("subject_code") Integer subjectId) {
        List<Topic> topics = topicRepository.findBySubjectIdAndActive(subjectId, true);

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

    @PostMapping("change_password.php")
    public ResponseEntity<MessageDTO> changePassword(@RequestBody ChangePasswordDTO dto) {
        Student student = getStudent();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(student.getLogin(), dto.getOld_password()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new MessageDTO("Wrong old password"));
        }
        student.setPassword("{bcrypt}" + BCrypt.hashpw(dto.getNew_password(), BCrypt.gensalt()));
        studentRepository.save(student);
        return ResponseEntity.ok(new MessageDTO("Пароль змінений успішно"));
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
        subject.setActive(true);
        subjectRepository.save(subject);
        return ResponseEntity.ok(new SubjectDTO(subject.getId(), subject.getName()));
    }

    @PostMapping("delete_subject.php")
    public ResponseEntity<DeleteMessageDTO> deleteSubject(@RequestBody DeleteSubjectDTO dto) {
        Optional<Subject> subjectOptional = subjectRepository.findById(dto.getSubject_k());
        if (subjectOptional.isPresent()) {
            Subject subject = subjectOptional.get();
            subject.setActive(false);
            subjectRepository.save(subject);
            return ResponseEntity.ok().body(new DeleteMessageDTO(subject.getId(), "Видалення успішне"));
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new DeleteMessageDTO(dto.getSubject_k(), "Видалення не виконано. Такого предмету не існую в базі даних"));
    }

    @PostMapping("delete_topic.php")
    public ResponseEntity<DeleteMessageDTO> deleteTopic(@RequestBody DeleteTopicDTO dto) {
        Optional<Topic> topicOptional = topicRepository.findByIdAndStudent(dto.getTopic_id(), getStudent());
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            topic.setActive(false);
            topicRepository.save(topic);
            return ResponseEntity.ok(new DeleteMessageDTO(topic.getId(), "Видалення успішне"));
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new DeleteMessageDTO(dto.getTopic_id(), "Не можливо видалити. За цією темою вже існують зайняття"));
    }

    @GetMapping("get_efficiency.php")
    public EfficiencyDTO getEfficiency() {
        return lessonRepository.getEfficiency(getStudent().getId());
    }

    @GetMapping("get_history.php")
    public ResponseEntity<List<HistoryDTO>> getHistory(HistoryRequestDTO dto) {
        if (dto.getGroup()) {
            return ResponseEntity.ok(lessonRepository.getGroupedHistory(
                    dto.getSubject(),
                    getStudent().getId(),
                    LessonStatus.FINISHED.getId(),
                    dto.getFrom_date().isEmpty() ? null : dto.getFrom_date(),
                    dto.getTo_date().isEmpty() ? null : dto.getTo_date()
            ));
        } else {
            return ResponseEntity.ok(lessonRepository.getHistory(
                    dto.getSubject(),
                    getStudent().getId(),
                    LessonStatus.FINISHED.getId(),
                    dto.getFrom_date().isEmpty() ? null : dto.getFrom_date(),
                    dto.getTo_date().isEmpty() ? null : dto.getTo_date()
            ));
        }
    }

    @GetMapping("get_statistics.php")
    public List<StatisticDTO> getStatistic() {
        return lessonRepository.getStatistic(getStudent().getId());
    }

    @GetMapping("get_statistics_for_days.php")
    public List<StatisticForDaysDTO> getStatisticForDays() {
        return lessonRepository.getStatisticForDays(getStudent().getId());
    }

    @GetMapping("get_subject_that_not_learn_yesterday.php")
    public List<SubjectThatNotLearnYesterdayDTO> getStatisticThatNotLearnYesterday() {
        return subjectRepository.getSubjectThatNotLearnYesterday(getStudent().getId(), LessonStatus.FINISHED.getId());
    }

    @GetMapping("get_user_info.php")
    public UserInfoDTO getUserInfo() {
        Student student = getStudent();
        return new UserInfoDTO(student.getLogin(), student.getName(), student.getDescription());
    }

    @PostMapping("sign_up.php")
    public ResponseEntity<SignUpOutDTO> signUp(@RequestBody SignUpInDTO dto) {
        Optional<Student> studentWithSameLoginOptional = studentRepository.findByLogin(dto.getLogin());
        if (!studentWithSameLoginOptional.isPresent()) {
            Student student = new Student();
            student.setLogin(dto.getLogin());
            student.setEmail(dto.getEmail());
            student.setName(dto.getName());
            student.setPassword("{noop}" + dto.getPassword());
            student.setDescription("");
            student.setPasswordHash("");
            student.setRole(1);
            Student savedStudent = studentRepository.save(student);
            return ResponseEntity.ok(new SignUpOutDTO(savedStudent.getId(), "successful"));
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new SignUpOutDTO(null, "already exist with login " + dto.getLogin()));
    }

    @PostMapping("start_lesson.php")
    @Transactional
    public MessageDTO startLesson(@RequestBody StartLessonDTO dto) throws NotFoundException {
        Subject subject = subjectRepository.findById(dto.getSubject())
                .orElseThrow(() -> new NotFoundException("Subject not exist with id = " + dto.getSubject()));

        Topic topic;
        Optional<Topic> topicOptional =  topicRepository.findByNameAndStudent(dto.getTheme(), getStudent());
        if (topicOptional.isPresent()) {
            topic = topicOptional.get();
        } else {
            Topic newTopic = new Topic();
            newTopic.setName(dto.getTheme());
            newTopic.setStudent(getStudent());
            newTopic.setActive(true);
            newTopic.setSubject(subject);
            topic = topicRepository.save(newTopic);
        }
        Lesson lesson = new Lesson();
        lesson.setSubject(subject);
        lesson.setStudent(getStudent());
        lesson.setLessonStatus(LessonStatus.ACTIVE);
        lesson.setTopic(topic);
        lesson.setDateStart(LocalDateTime.now());
        lesson.setDateEnd(LocalDateTime.now());
        Lesson savedLesson = lessonRepository.save(lesson);
        return new MessageDTO("Insert successful. New lesson ID = [" + savedLesson.getId() + "]");
    }

    @PostMapping("success_lesson.php")
    public Integer finishLesson(@RequestBody SuccessLessonDTO dto) throws NotFoundException {
        Lesson lesson = lessonRepository.findById(dto.getLesson_id())
                .orElseThrow(() -> new NotFoundException("Lesson not found with id = " + dto.getLesson_id()));

        lesson.setLessonStatus(LessonStatus.FINISHED);
        lesson.setDateEnd(LocalDateTime.now());
        lessonRepository.save(lesson);
        return lesson.getId();
    }

    @GetMapping({"get_user_token.php"})
    @PostMapping({"login.php"})
    public ResponseEntity<MessageDTO> noLongerSupported() {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new MessageDTO("Endpoint exist, but not longer supported"));
    }

    private Student getStudent() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (Student) principal;
    }

}
