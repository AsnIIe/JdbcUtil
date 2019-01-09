package com.asniie.utils.sqlite.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.asniie.utils.LogUtil;
import com.asniie.utils.sqlite.annotations.param;

public final class Formatter {
    public static String format(Method method, String str, Object[] objs) {
        String sql = str;

        Annotation[][] annotationsArray = method.getParameterAnnotations();

        if (annotationsArray == null || annotationsArray.length == 0) {
            return sql;
        }

        for (int i = 0; i < annotationsArray.length; i++) {
            Annotation[] annotations = annotationsArray[i];

            for (Annotation annotation : annotations) {
                if (annotation instanceof param) {
                    param param = (param) annotation;
                    String name = param.value();

                    sql = formatFromObj(name, sql, objs[i]);

                    if (sql.equals(str)) {
                        String regex = String.format("\\$\\s?\\{\\s?%s\\s?\\}", name);
                        return sql.replaceAll(regex, objs[i].toString());
                    }
                }
            }
        }

        return sql;
    }

    private static String formatFromObj(String className, String str, Object object) {
        Pattern pattern = Pattern.compile("\\$\\s?\\{\\s?([^}]+)\\s?\\.\\s?([^}]+)\\s?\\}");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            if (className.equals(matcher.group(1).trim())) {

                Object value = getValue(object, matcher.group(2));

                if (value != null) {
                    str = str.replace(matcher.group(), String.valueOf(value));
                }
                LogUtil.debug("sql-->" + str);
            }
        }
        return str;
    }

    private static Object getValue(Object object, String name) {
        try {
            Class<?> clazz = object.getClass();

            Method method = clazz.getMethod(getterMethodName(name), null);

            return method.invoke(object, null);
        } catch (Exception e) {
            LogUtil.debug(e);
        }

        return null;
    }

    private static String getterMethodName(String methodName) {
        methodName = methodName.replaceAll("\\s", "");

        char ch = methodName.charAt(0);
        String set = "get".concat(String.valueOf(Character.toUpperCase(ch)));
        return methodName.replaceFirst(String.valueOf(ch), set);
    }
}
