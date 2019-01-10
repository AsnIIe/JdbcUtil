package com.asniie.utils.sqlite.interceptors;

import java.lang.reflect.Type;

/*
 * Created by XiaoWei on 2019/1/10.
 */
public class AbstractInterceptor implements Interceptor {
    @Override
    public Object intercept(String[] sqls, ExecType type, Type returnType) {
        return null;
    }
}
