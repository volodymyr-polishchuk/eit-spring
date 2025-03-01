package com.volodymyrpo.eit.lessons;

import com.volodymyrpo.eit.lessons.status.LessonStatus;
import com.volodymyrpo.eit.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {

    List<Lesson> findByStudentAndLessonStatus(Student student, LessonStatus active);
}
