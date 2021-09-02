package com.small.biznez.Controller;

import com.small.biznez.Dto.StudentDto;
import com.small.biznez.Entity.Student;
import com.small.biznez.ResponsePojo.ResponsePojo;
import com.small.biznez.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentApi {
    @Autowired
    private StudentService studentService;


    @PostMapping("/create")
    public ResponsePojo<Student> createStudent(@RequestBody StudentDto studentDto){
        return studentService.createStudent(studentDto);
    }

    @GetMapping("/allstudents")
    public ResponsePojo<List<Student>> getAllStudents(){
        return studentService.getAllStudents();
    }

    @GetMapping("/searchById/{id}")
    public ResponsePojo<Student> getStudentById(@PathVariable Long id){
        return studentService.getStudentById(id);
    }

    @GetMapping("/matricsearch/{matNum}")
    public ResponsePojo<Student> getStudentByMatricNo(@PathVariable("matNum") String matricNumber){
        return studentService.getStudentByMatricNo(matricNumber);
    }

    @GetMapping("/firstNameSearch/{firstName}")
    public ResponsePojo<List<Student>> searchByFname(@PathVariable String firstName){
        return studentService.searchByFname(firstName);
    }

    @GetMapping("/lastNameSearch/{lastName}")
    public ResponsePojo<List<Student>> searchByLname(@PathVariable String lastName) {
        return studentService.searchByLname(lastName);
    }


    //Dynamic Search using Querydsl method
    //This is a dynamic function to search for students with multiple search contents(Querydsl)
    @GetMapping("/dynamicSearch")
    public ResponsePojo<Page<Student>> dynamicSearch(@RequestParam(name = "firstName", required = false ) String firstName,
                                                     @RequestParam(name = "lastName", required = false) String lastName,
                                                     @RequestParam(name = "matricNumber", required = false) String matricNumber,
                                                     Pageable pageable){
        return studentService.dynamicSearch(firstName, lastName, matricNumber, pageable);
    }


    @PutMapping("/updateStudent")
    public ResponsePojo<Student> updateStudent(@RequestBody StudentDto studentDto){
        return studentService.updateStudent(studentDto);
    }


    //Update Student detail using Querydsl
    @PutMapping("/adminUpdate")
    public ResponsePojo<Student> adminUpdate(@RequestBody StudentDto studentDto){
        return studentService.adminUpdate(studentDto);
    }

}
