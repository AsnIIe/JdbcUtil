package com.asniie.utils.sqlite.core;

import com.asniie.utils.sqlite.interceptors.Interceptor;
import com.asniie.utils.sqlite.interceptors.InterceptorChain;
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

    private InstanceProxy() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz) {
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InstanceProxy());
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
                    } else {
                        return execUpdate((update) annotation, null, null, returnType);
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

        return InterceptorChain.intercept(new String[]{sql}, Interceptor.ExecType.UPDATE, returnType);
    }

    private Object execTransaction(update update, Annotation[][] paramAnnotations, List<Object> objects, Type returnType) {
        int size = objects.size();
        String[] sqls = new String[size];

        for (int i = 0; i < size; i++) {
            sqls[i] = AnnotationParser.parseSQL(update.value(), paramAnnotations, new Object[]{objects.get(i)});
        }

        return InterceptorChain.intercept(sqls, Interceptor.ExecType.UPDATE, returnType);
    }

    private Object execQuery(query query, Annotation[][] paramAnnotations, Object[] params, Type returnType) {
        String sql = AnnotationParser.parseSQL(query.value(), paramAnnotations, params);

        return InterceptorChain.intercept(new String[]{sql}, Interceptor.ExecType.QUERY, returnType);
    }
}
