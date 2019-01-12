package com.asniie.utils.sql;

/*
 * Created by XiaoWei on 2019/1/13.
 */
public final class SqlEscape {
    public static String escape(String str) {
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

    public static String unescape(String str) {
        str = str.replace("//", "/");
        str = str.replace("''", "'");
        str = str.replace("/[", "[");
        str = str.replace("/]", "]");
        str = str.replace("/%", "%");
        str = str.replace("/&", "&");
        str = str.replace("/_", "_");
        str = str.replace("/(", "(");
        str = str.replace("/)", ")");
        return str;
    }
}
