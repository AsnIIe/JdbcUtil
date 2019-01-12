package com.asniie.utils.sqlite.core;

import com.asniie.utils.sqlite.annotations.query;
import com.asniie.utils.sqlite.annotations.update;
import com.asniie.utils.sqlite.exception.DataBaseException;
import com.asniie.utils.sqlite.interceptors.Interceptor;
import com.asniie.utils.sqlite.interceptors.InterceptorChain;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/*
 * Created by XiaoWei on 2019/1/12.
 */
public final class ObjectFactory implements InvocationHandler {

    private final ParamParser mParamParser = new ParamParser();

    private ObjectFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz) {
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new ObjectFactory());
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws DataBaseException {
        if (proxy.getClass().equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Exception e) {
                throw new DataBaseException(e);
            }
        }
        return exec(method, args);
    }

    @SuppressWarnings("unchecked")
    private Object exec(Method method, Object[] objects) {

        Annotation[] annotations = method.getAnnotations();

        if (annotations != null && annotations.length > 0) {
            Annotation[][] paramAnnotations = method.getParameterAnnotations();

            Type returnType = method.getGenericReturnType();

            for (Annotation annotation : annotations) {
                String sqlTemp = null;
                Interceptor.ExecType execType = null;
                if (annotation instanceof update) {
                    sqlTemp = ((update) annotation).value();
                    execType = Interceptor.ExecType.UPDATE;

                } else if (annotation instanceof query) {
                    sqlTemp = ((query) annotation).value();
                    execType = Interceptor.ExecType.QUERY;
                } else {
                    continue;
                }

                String[] sqls = mParamParser.parseParamsToSQL(sqlTemp, paramAnnotations, objects);

                return InterceptorChain.intercept(sqls, execType, returnType);
            }
        }

        return null;
    }
}
