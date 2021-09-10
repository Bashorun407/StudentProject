package com.small.biznez.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.small.biznez.Dto.StudentDto;
import com.small.biznez.Entity.QStudent;
import com.small.biznez.Entity.Student;
import com.small.biznez.Exception.ApiException;
import com.small.biznez.Reppo.StudentReppo;
import com.small.biznez.ResponsePojo.ResponsePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentReppo studentReppo;

    @Autowired
    private EntityManager entityManager;


    //Method to create Student details
    public ResponsePojo<Student> createStudent(StudentDto studentDto){
        if(ObjectUtils.isEmpty(studentDto))
            throw new ApiException("Pay Load is Empty");

        QStudent qStudent = QStudent.student;
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(qStudent.matricNo.equalsIgnoreCase(studentDto.getMatricNo()));

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        long count = jpaQueryFactory.selectFrom(qStudent).where(predicate).fetchCount();

        if(count>0)
            throw  new ApiException(String.format("Student with this matric number %s exists", studentDto.getMatricNo()));

        Student student=new Student();
        student.setSerialNumber(new Date().getTime());
        student.setFname(studentDto.getFname());
        student.setLname(studentDto.getLname());
        student.setCourse(studentDto.getCourse());
        student.setMatricNo(studentDto.getMatricNo());

        Student newStudent = studentReppo.save(student);
        ResponsePojo<Student> responsePojo = new ResponsePojo<>();
        responsePojo.setData(newStudent);
        responsePojo.setMessage("Student Detail Successfully Created!");

        return responsePojo;
    }

    public ResponsePojo<Student> getStudentById(Long id){

        Optional<Student> studentOptional = studentReppo.findById(id);
        studentOptional.orElseThrow(()->new ApiException("There is no student with the specified id"));

        QStudent qStudent = QStudent.student;
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(qStudent.id.eq(id));

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<Student> jpaQuery = jpaQueryFactory.selectFrom(qStudent)
                .where(predicate);

        Student newStudent = jpaQuery.fetchFirst();
        ResponsePojo<Student> responsePojo = new ResponsePojo<>();
        responsePojo.setData(newStudent);
        responsePojo.setMessage(String.format("Student with with id %s found!!",studentReppo.findById(id)));

        return responsePojo;
    }

    public ResponsePojo<List<Student>> getAllStudents(){
        List<Student> allStudents = studentReppo.findAll();
        if(allStudents.isEmpty())
            throw new ApiException("This is an Empty List!!");

        ResponsePojo<List<Student>> responsePojo = new ResponsePojo<>();
        responsePojo.setData(allStudents);
        responsePojo.setMessage("List of Students Found!!");
        return  responsePojo;
    }

    //Method to search student by Matric Number
    public ResponsePojo<Student> getStudentByMatricNo(String matricNumber){
        Optional<Student> studentOptional= studentReppo.findBymatricNo(matricNumber);
        studentOptional.orElseThrow(()->new RuntimeException("The Matric Number Entered is Invalid!!"));

        ResponsePojo<Student> responsePojo = new ResponsePojo<>();
        responsePojo.setData(studentOptional.get());
        responsePojo.setMessage(String.format("Student with Matric Number %s found!", matricNumber));
        return responsePojo;
    }

    //Method to search student by First Name
    public ResponsePojo<List<Student>> searchByFname(String firstName){
        List<Student> studentList = studentReppo.findByfname(firstName);

        if(studentList.isEmpty())
            throw new ApiException("Student with this First Name not found");

        ResponsePojo<List<Student>> responsePojo = new ResponsePojo<>();
        responsePojo.setData(studentList);
        responsePojo.setMessage("Student(s) with this name found!!");
        return responsePojo;
    }

    //Method to search student by Last Name
    public ResponsePojo<List<Student>> searchByLname(String lastName) {
        List<Student> studentList = studentReppo.findBylname(lastName);

        if(studentList.isEmpty())
            throw new ApiException("Student(s) with this Last name not found!!");

        ResponsePojo<List<Student>> responsePojo = new ResponsePojo<>();
        responsePojo.setData(studentList);
        responsePojo.setMessage("Student(s) With this Last name found!!");
        return responsePojo;
    }


    //This is a dynamic function to search for students with multiple search contents(Querydsl)
    public ResponsePojo<Page<Student>> dynamicSearch(String firstName, String lastName, String matricNumber, Pageable pageable){

        QStudent qStudent = QStudent.student;
        BooleanBuilder predicate = new BooleanBuilder();

        if(StringUtils.hasText(firstName))
            predicate.and(qStudent.fname.likeIgnoreCase("%"+ firstName + "%" ));

        if(StringUtils.hasText(lastName))
            predicate.and(qStudent.lname.likeIgnoreCase("%"+lastName+"%"));

        if(StringUtils.hasText(matricNumber))
            predicate.and(qStudent.matricNo.equalsIgnoreCase(matricNumber));

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<Student> jpaQuery = jpaQueryFactory.selectFrom(qStudent)
                .where(predicate)
                .orderBy(qStudent.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        //Introducing the ResponsePojo
        Page<Student> studentPage = new PageImpl<>(jpaQuery.fetch(), pageable, jpaQuery.fetchCount());

        ResponsePojo<Page<Student>> responsePojo = new ResponsePojo<>();
        responsePojo.setData(studentPage);
        responsePojo.setMessage("Student detail found!!");

        return responsePojo;
    }

    //Repeating search here using Querydsl just to practice what I've learnt
    public ResponsePojo<Page<Student>> searchStudent(String firstName, String lastName, String matricNumber, Pageable pageable){
        QStudent qStudent = QStudent.student;
        BooleanBuilder predicate = new BooleanBuilder();

        if(StringUtils.hasText(firstName))
            predicate.and(qStudent.fname.likeIgnoreCase("%" + firstName +"%"));

        if(StringUtils.hasText(lastName))
            predicate.and(qStudent.lname.likeIgnoreCase("%" + lastName + "%"));

        if(StringUtils.hasText(matricNumber))
            predicate.and(qStudent.matricNo.equalsIgnoreCase(matricNumber));

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<Student> jpaQuery = jpaQueryFactory.selectFrom(qStudent)
                .where(predicate)
                .orderBy(qStudent.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        Page<Student> studentPage =new PageImpl<>(jpaQuery.fetch(), pageable, jpaQuery.fetchCount());

        ResponsePojo<Page<Student>> responsePojo = new ResponsePojo<>();
        responsePojo.setData(studentPage);
        responsePojo.setMessage("Students Search Successful!!");

        return responsePojo;
    }


    //To update Student details
    public ResponsePojo<Student> updateStudent(StudentDto studentDto){
        if(ObjectUtils.isEmpty(studentDto.getMatricNo()))
            throw new RuntimeException("Pay Load is Empty");

        Optional<Student> studentOptional=studentReppo.findById(studentDto.getId());
        studentOptional.orElseThrow(()->new RuntimeException(String.format("The Student with this id %s not found!!"
                ,studentDto.getId() )));

        Student student=studentOptional.get();
        student.setSerialNumber(new Date().getTime());
        student.setFname(studentDto.getFname());
        student.setLname(studentDto.getLname());
        student.setMatricNo(studentDto.getMatricNo());
        student.setCourse(studentDto.getCourse());

        Student updatedStudent = studentReppo.save(student);

        ResponsePojo<Student> responsePojo = new ResponsePojo<>();
        responsePojo.setData(updatedStudent);
        responsePojo.setMessage("Student details updated!!");
        return responsePojo;
    }


    //Update Student detail using Querydsl
    public ResponsePojo<Student> adminUpdate(StudentDto studentDto){

        // TODO Matric Number is Required for update
        if(ObjectUtils.isEmpty(studentDto.getMatricNo()))
            throw new ApiException("Matric Number is Required for update...check Matric Number and ID");

        Optional<Student> studentOptional1= studentReppo.findById(studentDto.getId());
        studentOptional1.orElseThrow(()->new ApiException(String.format("Student with this ID %s not found!",
                studentDto.getId())));

        Optional<Student> studentOptional2= studentReppo.findBymatricNo(studentDto.getMatricNo());
        studentOptional2.orElseThrow(() -> new ApiException(String.format("Student with this Matric Number %s Not Found!!",
                studentDto.getMatricNo())));

        Student record1=studentOptional1.get();
        Student record2 = studentOptional2.get();
        if(record1!=record2)
            throw new ApiException("Matric Number Entered for update is for another student!!");

        Student student = studentOptional1.get();

        student.setFname(studentDto.getFname());
        student.setLname(studentDto.getLname());
        student.setMatricNo(studentDto.getMatricNo());
        student.setCourse(studentDto.getCourse());

        Student newStudent = studentReppo.save(student);

        ResponsePojo<Student> responsePojo = new ResponsePojo<>();
        responsePojo.setData(newStudent);
        responsePojo.setMessage("Student detail updated");


        return responsePojo;
    }

    //writing another multiple search just to check that I remember the codes
    public ResponsePojo<Page<Student>> multipleSearch(String firstName, String lastName, String matricNo, Pageable pageable, Long newLikes, Long love){

        QStudent qStudent = QStudent.student;
        BooleanBuilder predicate = new BooleanBuilder();

        if(StringUtils.hasText(firstName))
            predicate.and(qStudent.fname.likeIgnoreCase("%" + firstName+ "%"));

        if(StringUtils.hasText(lastName))
            predicate.and(qStudent.lname.likeIgnoreCase("%" + lastName + "%"));

        if (StringUtils.hasText(matricNo))
            predicate.and(qStudent.matricNo.equalsIgnoreCase(matricNo));

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<Student> jpaQuery = jpaQueryFactory.selectFrom(qStudent)
                .where(predicate)
                .orderBy(qStudent.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        //Writing the block of code to check that "Likes" can be added from the user
        Long countLikes=jpaQuery.fetchFirst().getLikes();
        Long countLove=jpaQuery.fetchFirst().getLove();
        if(!ObjectUtils.isEmpty(newLikes)){
            countLikes +=newLikes;
            jpaQuery.fetchFirst().setLikes(countLikes);
            Student student = jpaQuery.fetchFirst();
            studentReppo.save(student);
        }

        //***Writing another block of code to check that "Love" can be added from the user
        if (!ObjectUtils.isEmpty(love)) {
            countLove += love;
            jpaQuery.fetchFirst().setLove(countLove);
            Student student = jpaQuery.fetchFirst();
            studentReppo.save(student);
        }


        Page<Student> studentPage = new PageImpl<>(jpaQuery.fetch(), pageable, jpaQuery.fetchCount());

        ResponsePojo<Page<Student>> responsePojo = new ResponsePojo<>();
        responsePojo.setData(studentPage);
        responsePojo.setMessage("Student detail(s) found!!");

        return responsePojo;

    }

    //Writing another update just for the sake of practice
    public ResponsePojo<Student> secondUpdate(StudentDto studentDto){
        if(ObjectUtils.isEmpty(studentDto.getMatricNo()))
            throw new ApiException("Update requires a valid matric number...enter correct Matric Number!");

        Optional<Student> studentOptional1= studentReppo.findById(studentDto.getId());
        studentOptional1.orElseThrow(()-> new ApiException(String.format("Student with this Id %s not found!!",
                studentDto.getId())));

        Optional<Student> studentOptional2 = studentReppo.findBymatricNo(studentDto.getMatricNo());
        studentOptional2.orElseThrow(()-> new ApiException(String.format("Student with matric number %s not found!!",
                studentDto.getMatricNo())));

        Student record1 =studentOptional1.get();
        Student record2 = studentOptional2.get();
        if(record1!=record2)
            throw new ApiException("The matric number entered is for a different Student!!");

        Student student = studentOptional1.get();
        student.setFname(studentDto.getFname());
        student.setLname(studentDto.getLname());
        student.setMatricNo(studentDto.getMatricNo());
        student.setCourse(studentDto.getCourse());

        Student newStudent = studentReppo.save(student);

        ResponsePojo<Student> responsePojo = new ResponsePojo<>();
        responsePojo.setData(newStudent);
        responsePojo.setMessage("Student details updated successfully!");

        return responsePojo;

    }

    //Just a querydsl version of search student by id
    public ResponsePojo<Student> getStudentUsingId(Long id){

        //Introducing Querydsl and predicate
        QStudent qStudent = QStudent.student;
        BooleanBuilder predicate = new BooleanBuilder();

        //Conditional if no id is entered
        if (ObjectUtils.isEmpty(id))
            throw new ApiException("Id is Empty...please input valid Id");

        //Conditional if incorrect id is entered
        Optional<Student> studentOptional = studentReppo.findById(id);
        studentOptional.orElseThrow(()->new ApiException(String.format("No Student with the Id %s found !!", id)));

        //Conditional if a correct Id is entered
        if(!ObjectUtils.isEmpty(id))
            predicate.and(qStudent.id.eq(id));

        //Introducing JpaQuery to query/search the database for the student with the specified id
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<Student> jpaQuery = jpaQueryFactory.selectFrom(qStudent)
                .where(predicate)
                .distinct();

        Student foundStudent = jpaQuery.fetchOne();

        //Introducing the Responsepojo
        ResponsePojo<Student> responsePojo = new ResponsePojo<>();
        responsePojo.setMessage("Student with the id specified found!!");
        responsePojo.setData(foundStudent);

        return responsePojo;
    }

    //method to return a list of students
    public ResponsePojo<Page<Student>> allStudents(Pageable pageable){

        List<Student> studentList = studentReppo.findAll();
        if(studentList.isEmpty())
            throw  new ApiException("No students found!!");

        QStudent qStudent= QStudent.student;
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(qStudent.id.isNotNull());

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<Student> jpaQuery = jpaQueryFactory.selectFrom(qStudent)
                .where(predicate)
                .orderBy(qStudent.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());


        Page<Student> page = new PageImpl<>(jpaQuery.fetch(), pageable, jpaQuery.fetchCount());

        ResponsePojo<Page<Student>> responsePojo = new ResponsePojo<>();
        responsePojo.setMessage("List of students found!!");
        responsePojo.setData(page);

        return responsePojo;

    }

}
