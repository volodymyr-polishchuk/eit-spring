package com.volodymyrpo.eit.topic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    List<Topic> findBySubjectId(Integer subjectId);
}
