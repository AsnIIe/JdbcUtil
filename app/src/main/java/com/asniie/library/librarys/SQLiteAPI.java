package com.asniie.library.librarys;

import com.asniie.utils.sqlite.annotations.database;
import com.asniie.utils.sqlite.annotations.param;
import com.asniie.utils.sqlite.annotations.query;
import com.asniie.utils.sqlite.annotations.update;

import java.util.List;

@database("database.db")
public interface SQLiteAPI {

    @update("create table if not exists student (id int,age int,name varchar(20))")
    int createTable();

    @query("select * from student where id = ${id};")
    Person queryById(@param("id") int name);

    @query("select * from student where age = ${age};")
    List<Person> queryByAge(@param("age") int age);

    @update("insert into student values(${student.id},'${person.name}',${person.age})")
    boolean insert(@param("person") Person person, @param("student") Student student);

    @update("insert into student (id,name,age) values (${person.id},'${person.name}',${person.age})")
    boolean insert2(@param("person") List<Person> persons);
}
