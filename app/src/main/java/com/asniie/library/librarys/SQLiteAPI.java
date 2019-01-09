package com.asniie.library.librarys;

import java.util.Map;

import com.asniie.utils.sqlite.annotations.sql;
import com.asniie.utils.sqlite.annotations.param;

public interface SQLiteAPI {
	@sql("select * from table where id = ${id };")
	public Map<String, String> queryById(@param("id") int name);

	@sql("insert into table (${student.id},'${person.name}',${person.age});")
	public Map<String, String> insert(@param("person") Person person,@param("student") Student student);

}
