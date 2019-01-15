package com.asniie.library.librarys;

import com.asniie.utils.sql.annotations.Param;
import com.asniie.utils.sql.annotations.Params;
import com.asniie.utils.sql.annotations.Query;
import com.asniie.utils.sql.annotations.Update;

import java.util.List;

public interface SQLiteAPI {

    @Update("create table if not exists student (id int,age int,name varchar(20))")
    boolean createTable();

    @Query("SELECT * FROM student WHERE id = ${id}")
    List<Student> queryById(@Param("id") int id);

    @Query("SELECT * FROM student WHERE name = '${teacher.students.${index}.name}' AND age IN (${numbers})")
    List<Student> queryStudentByTeacher(@Param("teacher") Teacher teacher,
                                        @Param("numbers") int[] age,
                                        @Param("index") int index);

    @Query("select * from student where age = ${age} and name = '${name}';")
    List<Person> query(@Param("name") String name, @Param("age") int age);

    @Update("insert into student (id,name,age) values(${student.id},'${person.name}',${person.age})")
    boolean insert(@Param("person") Person person, @Param("student") Student student);

    @Update("insert into student (id,name,age) values (${person.id},'${person.name}',${student.age})")
    int insertStudents(@Params("person") List<Person> persons, @Params("student") List<Student> students);
}
