package com.asniie.utils.sqlite.core;

import com.asniie.utils.LogUtil;
import com.asniie.utils.sqlite.annotations.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by XiaoWei on 2019/1/10.
 */
public final class AnnotationParser {
    private static final String REGEX_FIELD = "\\$\\s?\\{\\s?(([^}]+\\s?\\.\\s?[^}]+)+)\\s?\\}";

    private static final Pattern PATTERN_FIELD = Pattern.compile(REGEX_FIELD);

    public static String parseSQL(String temp, Annotation[][] paramAnnotations, Object[] params) {

        String sql = temp;

        if (check(temp, paramAnnotations, params)) {
            for (int i = 0; i < paramAnnotations.length; i++) {
                Annotation[] annotations = paramAnnotations[i];

                for (Annotation annotation : annotations) {
                    if (annotation instanceof param) {
                        param param = (param) annotation;
                        String key = param.value();

                        sql = parseField(key, sql, params[i]);
                        sql = parseKey(sql, key, String.valueOf(params[i]));
                    }
                }
            }
        }
        return sql;
    }

    private static String parseKey(String temp, String key, String value) {
        String regex = String.format("\\$\\s?\\{\\s?%s\\s?\\}", key);
        return temp.replaceAll(regex, value);
    }

    private static String parseField(String className, String temp, Object object) {
        Matcher matcher = PATTERN_FIELD.matcher(temp);
        while (matcher.find()) {
            String group = matcher.group(1).replaceAll("\\s", "");
            String[] attrs = group.split("\\.");
            if (className.equals(attrs[0])) {
                Object value = object;
                for (int i = 1; i < attrs.length; i++) {
                    value = accessValue(value, attrs[i]);
                }
                temp = temp.replace(matcher.group(), escape(String.valueOf(value)));
            }
        }
        return temp;
    }

    private static Object accessValue(Object object, String param) {
        try {
            return parseValue(object, param);
        } catch (Exception e) {
            LogUtil.debug(e);
        }
        return null;
    }

    private static String escape(String str) {

        str = str.replace("/", "//");
        str = str.replace("'", "''");
        str = str.replace("[", "/[");
        str = str.replace("]", "/]");
        str = str.replace("%", "/%");
        str = str.replace("&", "/&");
        str = str.replace("_", "/_");
        str = str.replace("(", "/(");
        str = str.replace(")", "/)");
        return str;
    }

    private static String parseMethodName(Object object, String methodName, boolean isBool) {
        methodName = methodName.replaceAll("\\s", "");

        char ch = methodName.charAt(0);
        String set = (isBool ? "is" : "get").concat(String.valueOf(Character.toUpperCase(ch)));
        return methodName.replaceFirst(String.valueOf(ch), set);
    }

    private static Object parseValue(Object object, String param) throws Exception {
        Class<?> clazz = object.getClass();
        Object obj = null;

        if (object instanceof List) {
            Method method = ArrayList.class.getDeclaredMethod("get", int.class);
            obj = method.invoke(object, Integer.valueOf(param).intValue());
        } else if (object instanceof Map) {
            Method method = Map.class.getDeclaredMethod("get", Object.class);
            obj = method.invoke(object, param);
        } else {
            Field field = clazz.getDeclaredField(param);

            Class<?> type = field.getType();

            boolean isBool = type.equals(boolean.class) || type.equals(Boolean.class);

            Method method = clazz.getMethod(parseMethodName(object, param, isBool), new Class<?>[]{});

            obj = method.invoke(object, new Object[]{});
        }

        return obj;
    }

    private static boolean check(String temp, Annotation[][] paramAnnotations, Object[] params) {
        boolean b1 = paramAnnotations != null && paramAnnotations.length != 0;
        boolean b2 = params != null && params.length != 0;
        boolean b3 = temp != null && temp.trim().length() != 0;

        return b1 && b2 && b3;
    }
}
