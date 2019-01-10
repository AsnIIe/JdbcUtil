package com.asniie.utils.sqlite.Interceptor;

import java.lang.reflect.Type;

/*
 * Created by XiaoWei on 2019/1/10.
 */
public interface Interceptor {
    enum ExecType {UPDATE, QUERY}

    Object intercept(String[] sqls, ExecType type, Type returnType);
}
