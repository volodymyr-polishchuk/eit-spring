package com.volodymyrpo.eit.topic;

import com.volodymyrpo.eit.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    List<Topic> findBySubjectIdAndActive(Integer subjectId, Boolean active);

    Optional<Topic> findByIdAndStudent(Integer id, Student student);

    Optional<Topic> findByNameAndStudent(String name, Student student);
}
