package com.volodymyrpo.eit.subject;

import com.volodymyrpo.eit.student.Student;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "subject")
@Data
public class Subject {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(targetEntity = Student.class)
    private Student student;

    @Column(name = "active")
    private Boolean active;

}
