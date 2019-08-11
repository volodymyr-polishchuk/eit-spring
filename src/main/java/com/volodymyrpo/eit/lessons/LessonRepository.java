package com.volodymyrpo.eit.lessons;

import com.volodymyrpo.eit.lessons.status.LessonStatus;
import com.volodymyrpo.eit.old.api.dto.EfficiencyDTO;
import com.volodymyrpo.eit.old.api.dto.HistoryDTO;
import com.volodymyrpo.eit.old.api.dto.StatisticDTO;
import com.volodymyrpo.eit.old.api.dto.StatisticForDaysDTO;
import com.volodymyrpo.eit.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {

    List<Lesson> findByStudentAndLessonStatus(Student student, LessonStatus active);

    @Query(value = "SELECT ((current_week.sum - previous_week.sum) / previous_week.sum) * 100 AS efficiency\n" +
            "  FROM (\n" +
            "        SELECT COALESCE(SUM(UNIX_TIMESTAMP(date_end) - UNIX_TIMESTAMP(date_start)), 0) AS sum\n" +
            "        FROM lesson\n" +
            "        WHERE UNIX_TIMESTAMP(date_start) > (UNIX_TIMESTAMP(CURDATE()) - 3600 * 24 * 7)\n" +
            "              AND student_id LIKE :student_id\n" +
            "       ) AS current_week\n" +
            "       INNER JOIN (\n" +
            "        SELECT COALESCE(SUM(UNIX_TIMESTAMP(date_end) - UNIX_TIMESTAMP(date_start)), 0) AS sum\n" +
            "        FROM lesson\n" +
            "        WHERE UNIX_TIMESTAMP(date_start) > (UNIX_TIMESTAMP(CURDATE()) - 3600 * 24 * 7 * 2)\n" +
            "              AND UNIX_TIMESTAMP(date_start) <= (UNIX_TIMESTAMP(CURDATE()) - 3600 * 24 * 7)\n" +
            "              AND student_id LIKE :student_id\n" +
            "       ) AS previous_week", nativeQuery = true)
    EfficiencyDTO getEfficiency(@Param("student_id") Integer id);

    @Query(value = "SELECT s.name AS subject_name,\n" +
            "       s.sum_seconds AS seconds,\n" +
            "       CONCAT(SEC_TO_TIME(s.sum_seconds)) AS formatted_time\n" +
            "  FROM (SELECT subject.name AS name,\n" +
            "              SUM(UNIX_TIMESTAMP(date_end) - UNIX_TIMESTAMP(date_start)) AS sum_seconds\n" +
            "         FROM lesson\n" +
            "              INNER JOIN subject ON subject.id = lesson.subject_id\n" +
            "        WHERE lesson.student_id LIKE :student_id\n" +
            "        GROUP BY lesson.subject_id) AS s\n" +
            " ORDER BY seconds DESC", nativeQuery = true)
    List<StatisticDTO> getStatistic(@Param("student_id") Integer id);

    @Query(value = "SELECT SUM(UNIX_TIMESTAMP(date_end) - UNIX_TIMESTAMP(date_start)) AS day_result,\n" +
            "       CAST(date_start AS DATE) AS date\n" +
            "  FROM lesson\n" +
            "       INNER JOIN subject ON subject.id = lesson.subject_id\n" +
            " WHERE UNIX_TIMESTAMP(CAST(date_start AS DATE)) > UNIX_TIMESTAMP(CURDATE()) - (60 * 60 * 24 * 7)\n" +
            "       AND lesson.student_id LIKE :student_id\n" +
            " GROUP BY CAST(date_start AS DATE)", nativeQuery = true)
    List<StatisticForDaysDTO> getStatisticForDays(@Param("student_id") Integer id);

    @Query(value = "SELECT subject.name AS subjectName,\n" +
            "       topic.name AS themeName,\n" +
            "       lesson.date_start AS dateStart,\n" +
            "       lesson.date_end as dateEnd,\n" +
            "       (UNIX_TIMESTAMP(date_end) - UNIX_TIMESTAMP(date_start)) AS time\n" +
            "FROM lesson\n" +
            "       INNER JOIN subject ON subject.id = lesson.subject_id\n" +
            "       INNER JOIN topic ON topic.id = lesson.topic_id\n" +
            "WHERE (:subject_id = 0 OR lesson.subject_id LIKE :subject_id)\n" +
            "      AND subject.student_id LIKE :student_id\n" +
            "      AND lesson.lesson_status_id LIKE :lesson_status_id\n" +
            "      AND (:from_date IS NULL OR lesson.date_start >= DATE(:from_date))\n" +
            "      AND (:to_date IS NULL OR lesson.date_start < DATE_ADD(DATE(:to_date), INTERVAL 1 DAY))\n" +
            "ORDER BY lesson.date_start DESC", nativeQuery = true)
    List<HistoryDTO> getHistory(
            @Param("subject_id") Integer subjectId,
            @Param("student_id") Integer studentId,
            @Param("lesson_status_id") Integer lessonStatusId,
            @Param("from_date") String fromDate,
            @Param("to_date") String toDate
    );

    @Query(value = "SELECT subject.name AS subjectName,\n" +
            "       topic.name AS themeName,\n" +
            "       ' ' AS dateStart,\n" +
            "       ' ' as dateEnd,\n" +
            "       SUM(UNIX_TIMESTAMP(date_end) - UNIX_TIMESTAMP(date_start)) AS time\n" +
            "FROM lesson\n" +
            "       INNER JOIN subject ON subject.id = lesson.subject_id\n" +
            "       INNER JOIN topic ON topic.id = lesson.topic_id\n" +
            "WHERE (:subject_id = 0 OR lesson.subject_id LIKE :subject_id)\n" +
            "      AND subject.student_id LIKE :student_id\n" +
            "      AND lesson.lesson_status_id LIKE :lesson_status_id\n" +
            "      AND (:from_date IS NULL OR lesson.date_start >= DATE(:from_date))\n" +
            "      AND (:to_date IS NULL OR lesson.date_start < DATE_ADD(DATE(:to_date), INTERVAL 1 DAY))\n" +
            "GROUP BY lesson.topic_id, subjectName\n" +
            "ORDER BY MIN(lesson.date_start) DESC", nativeQuery = true)
    List<HistoryDTO> getGroupedHistory(
            @Param("subject_id") Integer subjectId,
            @Param("student_id") Integer studentId,
            @Param("lesson_status_id") Integer lessonStatusId,
            @Param("from_date") String fromDate,
            @Param("to_date") String toDate
    );
}
