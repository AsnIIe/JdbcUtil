package com.asniie.utils.sqlite.core;

import com.asniie.utils.sqlite.annotations.database;
import com.asniie.utils.sqlite.annotations.query;
import com.asniie.utils.sqlite.annotations.update;
import com.asniie.utils.sqlite.exception.DataBaseException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

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
        Object obj = null;

        mDataBase.connect(parseClassAnnotation(method));

        Type returnType = method.getGenericReturnType();

        update update = method.getAnnotation(update.class);

        if (update != null) {

            List<Object> objects = parseList(args);

            if (objects != null) {
                mDataBase.beginTransaction();

                int code = 0;
                for (Object object : objects) {
                    String sql = Formatter.format(method, update.value(), new Object[]{object});
                    //LogUtil.debug("sql = " + sql);
                    code = mDataBase.update(sql);
                    if (code == 0) {
                        break;
                    }
                }

                if (code != 0) {
                    mDataBase.commit();
                }
                mDataBase.endTransaction();

                if (returnType.equals(boolean.class) || returnType.equals(Boolean.class)) {
                    obj = (code != 0);
                } else {
                    obj = code;
                }
            } else {
                String sql = Formatter.format(method, update.value(), args);
                obj = mDataBase.update(sql);
            }
        } else {
            query query = method.getAnnotation(query.class);
            if (query != null) {
                String sql = Formatter.format(method, query.value(), args);

                obj = mDataBase.query(sql, returnType);
            }
        }

        if (mDataBase != null && mDataBase.isOpen()) {
            mDataBase.close();
        }

        return obj;
    }

    private String parseClassAnnotation(Method method) {
        Class<?> owner = method.getDeclaringClass();

        database db = owner.getAnnotation(database.class);

        return db.value();
    }

    private List<Object> parseList(Object[] objects) {

        if (objects != null && objects.length == 1) {
            Object object = objects[0];
            if (object instanceof List) {
                return (List<Object>) object;
            } else if (object.getClass().isArray()) {
                return Arrays.asList((Object[]) object);
            }
        }
        return null;
    }
}
