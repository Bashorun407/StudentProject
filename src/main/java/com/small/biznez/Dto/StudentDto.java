package com.small.biznez.Dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class StudentDto {
    private Long id;

    private Long serialNumber;

    private String fname;

    private String lname;

    private String course;

    private String  matricNo;
}
