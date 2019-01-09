package com.asniie.utils.sqlite;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.asniie.utils.LogUtil;
import com.asniie.utils.sqlite.annotations.sql;
import com.asniie.utils.sqlite.core.Formater;

public final class InstanceProxy implements InvocationHandler {

    private Map<String, String> mCache = new HashMap<>();
    //private final String TAG = "InstanceProxy";

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz) {
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (proxy.getClass().equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        return proxyMethod(method, args);
    }

    private Object proxyMethod(Method method, Object[] args) {
        String sql = readAnnotations(method);

        sql = Formater.format(method, sql, args);

        Map<String, Object> map = exec(sql);
        return map;
    }

    private String readAnnotations(Method method) {
        String key = buildKey(method);
        String sqlStr = mCache.get(key);
        if (sqlStr == null) {
            sql sql = method.getAnnotation(sql.class);
            if (sql != null) {
                sqlStr = sql.value();
                mCache.put(key, sqlStr);
            }
        }

        return sqlStr;
    }

    private Map<String, Object> exec(String sql) {
        Map<String, Object> map = new HashMap<>();

        map.put("id", 3);
        map.put("name", "小明");
        map.put("age", 20);

        LogUtil.print("exec: sql-->" + sql);
        return map;
    }

    private String buildKey(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        String key = clazz.getName().concat(".").concat(method.getName());

        return key;
    }
}
