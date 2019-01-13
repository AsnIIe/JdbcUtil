package com.asniie.utils.sql.core;

import com.asniie.utils.LogUtil;
import com.asniie.utils.sql.SqlEscape;
import com.asniie.utils.sql.exception.ExpParseException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by XiaoWei on 2019/1/12.
 * (已经修复)不要嵌套太复杂
 * (已经修复)特别是不要在一个变量外嵌套两层表达式包裹。如${${index}}会出错，${mList.${index}}不出错
 * SQL需按语法书写
 */
public final class ExpParser {
    private final String REGEX = "\\$\\s*\\{(.+?)\\}(?!\\s*[.}])";

    private final Pattern PATTERN = Pattern.compile(REGEX);

    private final ValueReader mValueReader = new ValueReader();

    private Map<String, List<Object>> mParamMap = null;

    private int mIndex = 0;

    public String[] parseExpression(String sqlTemp, Map<String, List<Object>> paramMap, int sqlSize) throws ExpParseException {
        List<String> sqlList = new ArrayList<>();

        this.mParamMap = paramMap;

        Matcher matcher = PATTERN.matcher(sqlTemp);

        for (int i = 0; i < sqlSize; i++) {
            mIndex = i;
            String sql = sqlTemp;

            while (matcher.find()) {
                String body = matcher.group();
                String value = SqlEscape.escape(parseExp(body.replaceAll("\\s", "")));
                sql = sql.replace(body, value);
            }
            sqlList.add(sql);
            matcher.reset();//重置正则匹配
        }
        return sqlList.toArray(new String[]{});
    }

    //${teacher.students.${${${index}}}.name}
    private String parseExp(String expression) {
        Matcher matcher = PATTERN.matcher(expression);

        if (matcher.find()) {
            String unwrap = unwrap(matcher.group(1));
            LogUtil.debug("ExpParser : peek---->" + unwrap);
            ExpReader reader = new ExpReader(unwrap);
            String[] items = reader.peek();
            reader.close();

            Object object = findObjectByKey(items[0]);

            for (int i = 1; i < items.length; i++) {
                object = mValueReader.readValue(object, parseExp(items[i]));
            }
            return formatObject(object);
        }
        return expression;
    }

    private Object findObjectByKey(String key) {
        LogUtil.debug("ExpParser : objectKey = " + key);
        List<Object> objects = mParamMap.get(key);
        int size = objects.size();
        return size > mIndex ? objects.get(mIndex) : objects.get(size - 1);
    }

    private String unwrap(String peek) {
        if (peek.startsWith("${")) {
            Matcher matcher = PATTERN.matcher(peek);
            if (matcher.find()) {
                peek = unwrap(matcher.group(1));
            }
        }
        return peek;
    }

    private String formatObject(Object object) {
        StringBuilder builder = new StringBuilder();
        if (object.getClass().isArray()) {
            int length = Array.getLength(object);
            for (int j = 0; j < length; j++) {
                builder.append(Array.get(object, j));
                if (j != length - 1) {
                    builder.append(',');
                }
            }
        } else if (object instanceof List) {
            List objects = ((List) object);
            int size = objects.size();
            for (int j = 0; j < size; j++) {
                builder.append(Array.get(object, j));
                if (j != size - 1) {
                    builder.append(',');
                }
            }
        } else {
            return object.toString();
        }
        return builder.toString();
    }

}
