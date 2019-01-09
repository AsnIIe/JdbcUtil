package com.asniie.utils.sqlite.core;

import com.asniie.utils.sqlite.annotations.database;
import com.asniie.utils.sqlite.exception.DataBaseException;

import java.lang.reflect.Method;

/*
 * Created by XiaoWei on 2019/1/9.
 */
public interface DataBase {

    boolean isOpen();

    void close();

    int update(String sql);

    Object query(String sql);

    void connect(String path) throws DataBaseException;

}
