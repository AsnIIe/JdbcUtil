package com.asniie.utils.sql.core;

import com.asniie.utils.sql.annotations.Param;
import com.asniie.utils.sql.annotations.Params;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by XiaoWei on 2019/1/12.
 */
public final class ParamParser {
    private final ExpParser mExpParser = new ExpParser();

    public String[] parseSqls(String sqlTemp, Annotation[][] paramAnnotations, Object[] objects) {

        Map<String, List<Object>> paramMap = new HashMap<>();

        int sqlSize = 0;
        if (check(sqlTemp, paramAnnotations, objects)) {
            for (int i = 0; i < paramAnnotations.length; i++) {
                Annotation[] annotations = paramAnnotations[i];

                List<Object> paramArray = new ArrayList<>(5);
                String paramName = null;
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Param) {
                        Param paramAnnotation = (Param) annotation;
                        paramName = paramAnnotation.value();

                        paramArray.add(objects[i]);
                    } else if (annotation instanceof Params) {
                        Params paramAnnotation = (Params) annotation;
                        paramName = paramAnnotation.value();

                        Object paramValue = objects[i];
                        Class<?> paramType = paramValue.getClass();
                        if (paramType.isArray()) {
                            int length = Array.getLength(paramValue);
                            for (int j = 0; j < length; j++) {
                                paramArray.add(Array.get(paramValue, j));
                            }
                        } else if (List.class.isAssignableFrom(paramType)) {
                            paramArray = (List<Object>) paramValue;
                        }
                    }

                    if (paramName != null) {
                        int size = paramArray.size();
                        //以size最大的List为标准
                        sqlSize = sqlSize > size ? sqlSize : size;
                        paramMap.put(paramName, paramArray);
                    }
                }
            }
        }

        if (paramMap.isEmpty()) {
            return new String[]{sqlTemp};
        }

        return mExpParser.parseExpression(sqlTemp, paramMap, sqlSize);
    }

    private boolean check(String temp, Annotation[][] paramAnnotations, Object[] params) {
        boolean b1 = paramAnnotations != null && paramAnnotations.length != 0;
        boolean b2 = params != null && params.length != 0;
        boolean b3 = temp != null && temp.trim().length() != 0;

        return b1 && b2 && b3;
    }
}
