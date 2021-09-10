package com.small.biznez.Entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Student {
    @Id
    @Column(name = "ID", unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name ="Serial_Number")
    private Long serialNumber;

    @Column(name = "First_Name")
    private String fname;

    @Column(name = "Last_Name")
    private String lname;

    @Column(name = "Course_Study")
    private String course;

    @Column(name = "Matric_Number")
    private String matricNo;

    @Column(name = "Likes_For_Student")
    private Long likes;

    @Column(name = "Love_For_Student")
    private Long love;
}

