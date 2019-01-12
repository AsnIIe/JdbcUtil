package com.asniie.utils.sqlite.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by XiaoWei on 2019/1/12.
 */
public final class ExpParser {
    private final String REGEX = "\\$\\s*\\{(.+?)\\}(?!\\s*[.}])";

    private final Pattern PATTERN = Pattern.compile(REGEX);

    private final ValueReader mValueReader = new ValueReader();

    private Map<String, List<Object>> mParamMap = null;

    private int mIndex = 0;

    public String[] parseExpression(String sqlTemp, Map<String, List<Object>> paramMap) {
        List<String> sqlArray = new ArrayList<>();

        mParamMap = paramMap;

        Matcher matcher = PATTERN.matcher(sqlTemp);

        for (String className : paramMap.keySet()) {
            int size = paramMap.get(className).size();

            for (int i = 0; i < size; i++) {
                mIndex = i;
                String sql = sqlTemp;

                while (matcher.find()) {
                    String expression = matcher.group().replaceAll("\\s", "");
                    String value = escape(parseExp(expression));
                    sql = sql.replace(matcher.group(), value);
                }

                sqlArray.add(sql);
                matcher.reset();//重置正则匹配
            }
            break;//以的第一个List的size为准
        }
        //LogUtil.debug("sqls.size--> " + sqlArray.size());
        return sqlArray.toArray(new String[]{});
    }

    //${teacher.students.${index}.name}
    private String parseExp(String expression) {
        Matcher matcher = PATTERN.matcher(expression);

        if (matcher.find()) {
            List<String> tokens = new ArrayList<>();
            StringBuilder buffer = new StringBuilder();
            StringTokenizer tokenizer = new StringTokenizer(matcher.group(1), ".", false);
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken();
                if (token.startsWith("$")) {
                    buffer.append(token);
                    buffer.append(".");
                } else if (token.endsWith("}")) {
                    buffer.append(token);
                    tokens.add(buffer.toString());
                } else {
                    tokens.add(token);
                }
            }

            // LogUtil.debug(tokens);

            String key = tokens.get(0);
            List<Object> objects = mParamMap.get(key);
            int size = objects.size();
            Object object = size > mIndex ? objects.get(mIndex) : objects.get(size - 1);

            for (int i = 1; i < tokens.size(); i++) {
                object = mValueReader.readValue(object, parseExp(tokens.get(i)));
            }
            return object.toString();
        }
        return expression;
    }

    private String escape(String str) {
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
}
