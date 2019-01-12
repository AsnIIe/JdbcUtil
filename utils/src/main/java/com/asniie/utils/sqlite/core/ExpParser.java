package com.asniie.utils.sqlite.core;

import com.asniie.utils.sqlite.exception.ExpParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by XiaoWei on 2019/1/12.
 * 不要嵌套太复杂
 * 特别是不要在一个变量外嵌套两层表达式包裹。如${${index}}会出错，${array.${index}}不出错
 */
public final class ExpParser {
    private final String REGEX = "\\$\\s*\\{(.+?)\\}(?!\\s*[.}])";

    private final Pattern PATTERN = Pattern.compile(REGEX);

    private final ValueReader mValueReader = new ValueReader();

    private Map<String, List<Object>> mParamMap = null;

    private int mIndex = 0;

    public String[] parseExpression(String sqlTemp, Map<String, List<Object>> paramMap) throws ExpParseException {
        List<String> sqlList = new ArrayList<>();

        this.mParamMap = paramMap;

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
                //LogUtil.debug("sqls--> " + sql);
                sqlList.add(sql);
                matcher.reset();//重置正则匹配
            }
            break;//以的第一个List的size为准
        }
        return sqlList.toArray(new String[]{});
    }

    //${teacher.students.${index}.name}
    private String parseExp(String expression) {
        Matcher matcher = PATTERN.matcher(expression);

        if (matcher.find()) {
            List<String> tokens = new ArrayList<>();
            StringBuilder buffer = new StringBuilder();
            StringTokenizer tokenizer = new StringTokenizer(matcher.group(1), ".", false);

            int level = 0;
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken();
                //  LogUtil.debug(token);

                if (token.startsWith("$") && !token.endsWith("}")) {
                    level++;
                    buffer.append(token);
                    buffer.append('.');
                } else if (token.endsWith("}") && !token.startsWith("$")) {
                    level--;
                    buffer.append(token);
                    if (level == 0 || token.endsWith(buildEnds(level))) {
                        tokens.add(buffer.toString());
                        level = 0;
                    } else {
                        buffer.append('.');
                    }
                } else if (token.startsWith("$") && token.endsWith("}")) {
                    if (level == 0) {
                        tokens.add(token);
                    } else {
                        if (token.endsWith(buildEnds(level))) {
                            buffer.append(token);
                            tokens.add(buffer.toString());
                            level = 0;
                        } else {
                            buffer.append(token);
                            buffer.append(".");
                            level -= endsCount(token);
                        }
                    }
                } else {
                    if (level == 0) {
                        tokens.add(token);
                    } else {
                        buffer.append(token);
                        buffer.append(".");
                    }
                }
            }
            //LogUtil.debug(tokens);
            Object object = findObjectByKey(tokens.get(0));

            for (int i = 1; i < tokens.size(); i++) {
                object = mValueReader.readValue(object, parseExp(tokens.get(i)));
            }
            return object.toString();
        }
        return expression;
    }

    //计算‘}’数量
    private int endsCount(String token) {
        char[] chars = token.toCharArray();
        int count = 0;

        for (char ch : chars) {
            if (ch == '}') {
                count++;
            }
        }
        return count;
    }

    /*//计算等级
    private int countLevel(String token) {
        char[] chars = token.toCharArray();
        int level = 0;

        for (char ch : chars) {
            if (ch == '}') {
                level--;
            } else if (ch == '$') {
                level++;
            }
        }
        return level;
    }*/

    private String buildEnds(int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= count; i++) {
            builder.append('}');
        }
        return builder.toString();
    }

    private Object findObjectByKey(String key) {
        List<Object> objects = mParamMap.get(key);
        int size = objects.size();
        return size > mIndex ? objects.get(mIndex) : objects.get(size - 1);
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
