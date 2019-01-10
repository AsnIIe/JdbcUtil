package com.asniie.utils.sqlite.core;

import com.asniie.utils.sqlite.annotations.query;
import com.asniie.utils.sqlite.annotations.update;
import com.asniie.utils.sqlite.exception.DataBaseException;

import java.lang.annotation.Annotation;
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

    private Object exec(Method method, Object[] objects) {

        //连接数据库
        mDataBase.connect(AnnotationParser.parseUri(method.getDeclaringClass()));

        Annotation[] annotations = method.getAnnotations();

        if (annotations != null && annotations.length > 0) {
            Annotation[][] paramAnnotations = method.getParameterAnnotations();

            Type returnType = method.getGenericReturnType();

            for (Annotation annotation : annotations) {
                if (annotation instanceof update) {
                    if (objects != null && objects.length > 0) {
                        Object object = objects[0];

                        List<Object> params = null;
                        if (object instanceof List) {
                            params = (List<Object>) object;
                        } else if (object.getClass().isArray()) {
                            params = Arrays.asList((Object[]) object);
                        }

                        if (params == null) {
                            return execUpdate((update) annotation, paramAnnotations, objects, returnType);
                        } else {
                            return execTransaction((update) annotation, paramAnnotations, params, returnType);
                        }
                    }
                } else if (annotation instanceof query) {
                    return execQuery((query) annotation, paramAnnotations, objects, returnType);
                }
            }
        }

        return null;
    }

    private Object execUpdate(update update, Annotation[][] paramAnnotations, Object[] params, Type returnType) {
        String sql = AnnotationParser.parseSQL(update.value(), paramAnnotations, params);
        int result = mDataBase.update(sql);

        this.closeDb();

        if (needConvert(returnType)) {
            return result != 0;
        } else {
            return result;
        }
    }

    private Object execTransaction(update update, Annotation[][] paramAnnotations, List<Object> objects, Type returnType) {

        mDataBase.beginTransaction();

        int result = 0;
        for (Object object : objects) {
            String sql = AnnotationParser.parseSQL(update.value(), paramAnnotations, new Object[]{object});
            result = mDataBase.update(sql);
            if (result == 0) {
                break;
            }
        }

        if (result != 0) {
            mDataBase.commit();
        }
        mDataBase.endTransaction();

        this.closeDb();

        if (needConvert(returnType)) {
            return result != 0;
        } else {
            return result;
        }
    }

    private Object execQuery(query query, Annotation[][] paramAnnotations, Object[] params, Type returnType) {
        String sql = AnnotationParser.parseSQL(query.value(), paramAnnotations, params);

        Object object = mDataBase.query(sql, returnType);

        this.closeDb();
        return object;
    }

    private boolean needConvert(Type returnType) {
        return returnType.equals(boolean.class) || returnType.equals(Boolean.class);
    }

    private void closeDb() {
        if (mDataBase != null && mDataBase.isOpen()) {
            mDataBase.close();
        }
    }
}
