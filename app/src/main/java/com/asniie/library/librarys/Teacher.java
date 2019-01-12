package com.asniie.library.librarys;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/*
 * Created by XiaoWei on 2019/1/12.
 */
public class Teacher implements Serializable {
    private int id;
    private List<Student> students;
    private Map<String, Book> books;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Map<String, Book> getBooks() {
        return books;
    }

    public void setBooks(Map<String, Book> books) {
        this.books = books;
    }
}
