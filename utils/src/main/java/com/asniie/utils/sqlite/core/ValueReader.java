package com.asniie.utils.sqlite.core;

import com.asniie.utils.sqlite.exception.ValueReadException;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/*
 * Created by XiaoWei on 2019/1/12.
 */
public final class ValueReader {

    public Object readValue(Object object, String param) throws ValueReadException {
        try {
            Class<?> clazz = object.getClass();

            if (object instanceof List) {
                return ((List) object).get(Integer.valueOf(param).intValue());
            } else if (object instanceof Map) {
                return ((Map) object).get(param);
            } else if (object instanceof Serializable) {
                Field field = clazz.getDeclaredField(param);

                Class<?> type = field.getType();

                boolean isBool = type.equals(boolean.class) || type.equals(Boolean.class);

                Method method = clazz.getMethod(parseSerializableMethodName(object, param, isBool), new Class<?>[]{});

                return method.invoke(object, new Object[]{});
            }
        } catch (Exception e) {
            throw new ValueReadException(e);
        }

        throw new IllegalArgumentException("the target interface's method params must be java.util.List or java.util.Map or java.io.Serializable");
    }

    private String parseSerializableMethodName(Object object, String methodName, boolean isBool) {
        methodName = methodName.replaceAll("\\s", "");

        char ch = methodName.charAt(0);
        String set = (isBool ? "is" : "get").concat(String.valueOf(Character.toUpperCase(ch)));
        return methodName.replaceFirst(String.valueOf(ch), set);
    }
}
