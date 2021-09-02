package com.small.biznez.Reppo;

import com.small.biznez.Entity.Student;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentReppo extends JpaRepository<Student, Long> {

    List<Student> findByfname(String firstName);
    List<Student> findBylname(String lastName);
    Optional<Student> findBymatricNo(String matricNumber);
}
