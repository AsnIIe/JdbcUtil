package com.asniie.library.librarys;

import java.util.List;
import java.util.Map;

import com.asniie.utils.sqlite.annotations.database;
import com.asniie.utils.sqlite.annotations.query;
import com.asniie.utils.sqlite.annotations.update;
import com.asniie.utils.sqlite.annotations.param;

@database("database.db")
public interface SQLiteAPI {

    @update("create table if not exists student (id int,age int,name varchar(20))")
    int createTable();

    @query("select * from student where id = ${id};")
    Map<String, String> queryById(@param("id") int name);

    @query("select * from student where age = ${age};")
    Map<String, String> queryByAge(@param("age") int age);

    @update("insert into student values(${student.id},'${person.name}',${person.age})")
    int insert(@param("person") Person person, @param("student") Student student);

    @update("insert into student (id,name,age) values (${person.id},'${person.name}',${person.age})")
    int insert2(@param("person") List<Person> persons);
}
