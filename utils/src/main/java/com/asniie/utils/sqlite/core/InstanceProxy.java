package com.asniie.utils.sqlite.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.asniie.utils.sqlite.annotations.database;
import com.asniie.utils.sqlite.annotations.query;
import com.asniie.utils.sqlite.annotations.update;
import com.asniie.utils.sqlite.exception.DataBaseException;

public final class InstanceProxy implements InvocationHandler {
    private DataBase mDataBase = null;

    public InstanceProxy(DataBase db) {
        mDataBase = db;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz) {
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws DataBaseException {
        try {
            if (proxy.getClass().equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }
            return exec(method, args);
        } catch (Throwable e) {
            throw new DataBaseException(e);
        }
    }

    private Object exec(Method method, Object[] args) {
        Object object = null;

        mDataBase.connect(parseClassAnnotation(method));

        update update = method.getAnnotation(update.class);

        if (update != null) {
            String sql = Formatter.format(method, update.value(), args);
            object = mDataBase.update(sql);
        } else {
            query query = method.getAnnotation(query.class);
            if (query != null) {
                String sql = Formatter.format(method, query.value(), args);
                object = mDataBase.query(sql);
            }
        }

        if (mDataBase != null && mDataBase.isOpen()) {
            mDataBase.close();
        }

        return object;
    }

    private String parseClassAnnotation(Method method) {
        Class<?> owner = method.getDeclaringClass();

        database db = owner.getAnnotation(database.class);

        return db.value();
    }
    /*

    private int update(DataBase db, String sql) {
        LogUtil.debug("exec: query-->" + sql);
        return db.update(sql);
    }

    private Object query(DataBase db, String sql) {
        Map<String, Object> map = new HashMap<>();

        map.put("id", 3);
        map.put("name", "小明");
        map.put("age", 20);

        LogUtil.debug("exec: sql-->" + sql);
        return map;
    }*/
}
