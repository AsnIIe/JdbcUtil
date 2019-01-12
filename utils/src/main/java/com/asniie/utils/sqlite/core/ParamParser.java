package com.asniie.utils.sqlite.core;

import com.asniie.utils.sqlite.annotations.param;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by XiaoWei on 2019/1/12.
 */
public final class ParamParser {
    private final ExpParser mExpParser = new ExpParser();

    public String[] parseParamsToSQL(String sqlTemp, Annotation[][] paramAnnotations, Object[] params) {

        Map<String, List<Object>> paramMap = new HashMap<>();

        if (check(sqlTemp, paramAnnotations, params)) {
            for (int i = 0; i < paramAnnotations.length; i++) {
                Annotation[] annotations = paramAnnotations[i];

                for (Annotation annotation : annotations) {
                    if (annotation instanceof param) {

                        param paramAnnotation = (param) annotation;
                        String className = paramAnnotation.value();
                        Object param = params[i];

                        Class<?> paramType = param.getClass();
                        List<Object> paramArray = new ArrayList<>(5);
                        if (paramType.isArray()) {
                            paramArray = Arrays.asList((Object[]) param);
                        } else if (List.class.isAssignableFrom(paramType)) {
                            paramArray = (List<Object>) param;
                        } else {
                            paramArray.add(param);
                        }

                        paramMap.put(className, paramArray);
                    }
                }
            }
        }

        if (paramMap.isEmpty()) {
            return new String[]{sqlTemp};
        }

        return mExpParser.parseExpression(sqlTemp, paramMap);
    }

    private boolean check(String temp, Annotation[][] paramAnnotations, Object[] params) {
        boolean b1 = paramAnnotations != null && paramAnnotations.length != 0;
        boolean b2 = params != null && params.length != 0;
        boolean b3 = temp != null && temp.trim().length() != 0;

        return b1 && b2 && b3;
    }
}
