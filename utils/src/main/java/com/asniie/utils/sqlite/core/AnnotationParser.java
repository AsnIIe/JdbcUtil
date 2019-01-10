package com.asniie.utils.sqlite.core;

import com.asniie.utils.LogUtil;
import com.asniie.utils.sqlite.annotations.database;
import com.asniie.utils.sqlite.annotations.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by XiaoWei on 2019/1/10.
 */
public final class AnnotationParser {
    private static final String REGEX_FIELD = "\\$\\s?\\{\\s?([^}]+)\\s?\\.\\s?([^}]+)\\s?\\}";

    private static final Pattern PATTERN_FIELD = Pattern.compile(REGEX_FIELD);

    public static String parseUri(Class<?> owner) {
        database db = owner.getAnnotation(database.class);
        return db.value();
    }

    public static String parseSQL(String temp, Annotation[][] paramAnnotations, Object[] params) {

        if (check(temp, paramAnnotations, params)) {
            for (int i = 0; i < paramAnnotations.length; i++) {
                Annotation[] annotations = paramAnnotations[i];

                for (Annotation annotation : annotations) {
                    if (annotation instanceof param) {
                        param param = (param) annotation;
                        String key = param.value();

                        Matcher matcher = PATTERN_FIELD.matcher(temp);

                        if (matcher.find()) {
                            return parseField(key, temp, params[i]);
                        } else {
                            return parseKey(temp, key, String.valueOf(params[i]));
                        }
                    }
                }
            }
        }
        return temp;
    }

    private static String parseKey(String temp, String key, String value) {
        String regex = String.format("\\$\\s?\\{\\s?%s\\s?\\}", key);
        return temp.replaceAll(regex, value);
    }

    private static String parseField(String className, String temp, Object object) {
        Matcher matcher = PATTERN_FIELD.matcher(temp);
        while (matcher.find()) {
            if (className.equals(matcher.group(1).trim())) {

                Object value = accessValue(object, matcher.group(2));

                if (value != null) {
                    temp = temp.replace(matcher.group(), String.valueOf(value));
                }
            }
        }
        return temp;
    }

    private static Object accessValue(Object object, String name) {
        try {
            Class<?> clazz = object.getClass();

            Field field = clazz.getDeclaredField(name);

            Class<?> type = field.getType();

            boolean isBool = type.equals(boolean.class) || type.equals(Boolean.class);

            Method method = clazz.getMethod(parseMethodName(name, isBool), new Class<?>[]{});

            return method.invoke(object, new Object[]{});
        } catch (Exception e) {
            LogUtil.debug(e);
        }

        return null;
    }

    private static String parseMethodName(String methodName, boolean isBool) {
        methodName = methodName.replaceAll("\\s", "");

        char ch = methodName.charAt(0);
        String set = (isBool ? "is" : "get").concat(String.valueOf(Character.toUpperCase(ch)));
        return methodName.replaceFirst(String.valueOf(ch), set);
    }

    private static boolean check(String temp, Annotation[][] paramAnnotations, Object[] params) {
        boolean b1 = paramAnnotations != null && paramAnnotations.length != 0;
        boolean b2 = params != null && params.length != 0;
        boolean b3 = temp != null && temp.trim().length() != 0;

        return b1 && b2 && b3;
    }
}
