package com.asniie.utils.sql.core;

import com.asniie.utils.sql.exception.ValueReadException;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/*
 * Created by XiaoWei on 2019/1/12.
 * 仅支持四种格式数据传入Array、List、Map、Serializable
 */
public final class ValueReader {

    public Object readValue(Object object, String param) throws ValueReadException {
        try {
            Class<?> clazz = object.getClass();
            if (clazz.isArray()) {
                return Array.get(object, Integer.valueOf(param));
            } else if (object instanceof List) {
                return ((List) object).get(Integer.valueOf(param));
            } else if (object instanceof Map) {
                return ((Map) object).get(param);
            } else if (object instanceof Serializable) {
                Field field = clazz.getDeclaredField(param);

                Class<?> type = field.getType();

                boolean isBool = type.equals(boolean.class) || type.equals(Boolean.class);

                Method method = clazz.getMethod(parseSerializableMethodName(param, isBool), new Class<?>[]{});

                return method.invoke(object, new Object[]{});
            }
        } catch (Exception e) {
            throw new ValueReadException(e);
        }

        throw new IllegalArgumentException("Interface method parameters must be Array or List or Map or Serializable.You passed in a " + object.getClass());
    }

    private String parseSerializableMethodName(String methodName, boolean isBool) {
        methodName = methodName.replaceAll("\\s", "");

        char ch = methodName.charAt(0);
        String get = (isBool ? "is" : "get").concat(String.valueOf(Character.toUpperCase(ch)));
        return methodName.replaceFirst(String.valueOf(ch), get);
    }
}
