package com.volodymyrpo.eit.lessons;

import com.volodymyrpo.eit.lessons.status.LessonStatus;
import com.volodymyrpo.eit.student.Student;
import com.volodymyrpo.eit.subject.Subject;
import com.volodymyrpo.eit.topic.Topic;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "lesson")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(targetEntity = Subject.class)
    private Subject subject;

    @ManyToOne(targetEntity = Topic.class)
    private Topic topic;

    @Column(name = "date_start")
    private LocalDateTime dateStart;

    @Column(name = "date_end")
    private LocalDateTime dateEnd;

    @Column(name = "lesson_status_id")
    private LessonStatus lessonStatus;

    @ManyToOne(targetEntity = Student.class)
    private Student student;
}
