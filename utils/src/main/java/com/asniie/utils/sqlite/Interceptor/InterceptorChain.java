package com.asniie.utils.sqlite.Interceptor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by XiaoWei on 2019/1/10.
 */
public final class InterceptorChain {

    private static final List<Interceptor> mInterceptorsors = new ArrayList<>(10);

    private InterceptorChain() {
    }

    static {
        addInterceptor(new LogInterceptor());
    }

    public static void addInterceptor(Interceptor interceptor) {
        mInterceptorsors.add(interceptor);
    }

    public static boolean removeInterceptor(Interceptor interceptor) {
        return mInterceptorsors.remove(interceptor);
    }

    public static Interceptor removeInterceptor(int index) {
        return mInterceptorsors.remove(index);
    }

    public static Object intercept(String[] sqls, Interceptor.ExecType type, Type returnType) {
        Object object = null;
        for (Interceptor interceptor : mInterceptorsors) {
            if (object == null) {
                object = interceptor.intercept(sqls, type, returnType);
            }
        }
        return object;
    }
}
