package com.volodymyrpo.eit.topic;

import com.volodymyrpo.eit.student.Student;
import com.volodymyrpo.eit.subject.Subject;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "topic")
@Data
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(targetEntity = Subject.class)
    private Subject subject;

    @ManyToOne(targetEntity = Student.class)
    private Student student;

    @Column(name = "active")
    private Boolean active;
}
