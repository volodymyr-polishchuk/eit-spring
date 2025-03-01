package com.volodymyrpo.eit.student;

import com.volodymyrpo.eit.student.Student;
import com.volodymyrpo.eit.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("jwtUserDetailsService")
public class StudentService implements UserDetailsService {


    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student user = studentRepository.findByLogin(username);

        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        return user;
    }
}
