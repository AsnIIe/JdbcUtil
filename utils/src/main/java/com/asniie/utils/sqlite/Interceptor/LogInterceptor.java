package com.asniie.utils.sqlite.Interceptor;

import com.asniie.utils.LogUtil;

import java.lang.reflect.Type;

/*
 * Created by XiaoWei on 2019/1/10.
 */
public final class LogInterceptor extends AbstractInterceptor {
    public static boolean DEBUG = true;

    @Override
    public Object intercept(String[] sqls, ExecType type, Type returnType) {

        if (DEBUG) {
            for (String sql : sqls) {
                LogUtil.debug(sql);
            }
        }
        return super.intercept(sqls, type, returnType);
    }
}
