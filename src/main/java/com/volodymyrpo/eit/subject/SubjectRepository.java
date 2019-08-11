package com.volodymyrpo.eit.subject;

import com.volodymyrpo.eit.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    List<Subject> findByStudentAndActive(Student student, Boolean active);

    Subject findByName(String name);
}
