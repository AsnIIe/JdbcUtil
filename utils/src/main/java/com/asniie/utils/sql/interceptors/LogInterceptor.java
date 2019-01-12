package com.asniie.utils.sql.interceptors;

import com.asniie.utils.LogUtil;
import com.asniie.utils.sql.exception.DataBaseException;

import java.lang.reflect.Type;

/*
 * Created by XiaoWei on 2019/1/10.
 */
public final class LogInterceptor extends AbstractInterceptor {
    public static boolean DEBUG = true;

    @Override
    public Object intercept(String[] sqls, ExecType type, Type returnType) throws DataBaseException {

        if (DEBUG) {
            for (String sql : sqls) {
                LogUtil.debug("LogInterceptor : " + sql);
            }
        }
        return super.intercept(sqls, type, returnType);
    }
}
