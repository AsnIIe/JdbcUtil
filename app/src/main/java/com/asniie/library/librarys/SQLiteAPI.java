package com.asniie.library.librarys;

import com.asniie.utils.sqlite.annotations.param;
import com.asniie.utils.sqlite.annotations.query;
import com.asniie.utils.sqlite.annotations.update;

import java.util.List;

public interface SQLiteAPI {

    @update("create table if not exists student (id int,age int,name varchar(20))")
    boolean createTable();

    @query("SELECT * FROM student WHERE id = ${id}")
    List<Student> queryById(@param("id") int id);

    @query("SELECT * FROM student WHERE age = ${teacher.students.${teacher.id}.age}")
    List<Student> queryStudentByTeacher(@param("teacher") Teacher teacher, @param("index") int index);

    @query("select * from student where age = ${age} and name = '${name}';")
    List<Person> query(@param("name") String name, @param("age") int age);

    @update("insert into student (id,name,age) values(${student.id},'${person.name}',${person.age})")
    boolean insert(@param("person") Person person, @param("student") Student student);

    @update("insert into student (id,name,age) values (${person.id},'${person.name}',${student.age})")
    int insertStudents(@param("person") List<Person> persons, @param("student") List<Student> students);
}
