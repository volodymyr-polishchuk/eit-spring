package com.volodymyrpo.eit.subject;

import com.volodymyrpo.eit.old.api.dto.SubjectThatNotLearnYesterdayDTO;
import com.volodymyrpo.eit.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    List<Subject> findByStudentAndActive(Student student, Boolean active);

    Subject findByName(String name);

    @Query(value = "SELECT subject.name AS subjectName \n" +
            "  FROM subject \n" +
            " WHERE id NOT IN (SELECT lesson.subject_id \n" +
            "                   FROM lesson \n" +
            "                  WHERE (UNIX_TIMESTAMP(lesson.date_start) >= (UNIX_TIMESTAMP(CURDATE()) - 86400) \n" +
            "                        AND UNIX_TIMESTAMP(lesson.date_start) < UNIX_TIMESTAMP(CURDATE())) \n" +
            "                        AND lesson.lesson_status_id = :lesson_status_id\n" +
            "                  GROUP BY lesson.subject_id)\n" +
            "       AND student_id LIKE :student_id\n" +
            "       AND subject.active IS TRUE", nativeQuery = true)
    List<SubjectThatNotLearnYesterdayDTO> getSubjectThatNotLearnYesterday(
            @Param("student_id") Integer id,
            @Param("lesson_status_id") Integer lessonStatusId
    );
}
