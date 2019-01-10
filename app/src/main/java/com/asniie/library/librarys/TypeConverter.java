package com.asniie.library.librarys;

import com.asniie.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.internal.Primitives;

import java.lang.reflect.Type;

/*
 * Created by XiaoWei on 2019/1/10.
 */
public final class TypeConverter {
    private static final Gson mGson = new Gson();

    public static Object convert(Object src, Type to) {
        if (src == null) {
            return src;
        }

        if (Primitives.isPrimitive(to) || Primitives.isWrapperType(to) || to.equals(String.class)) {
            if (src instanceof Number) {
                if (Primitives.wrap((Class<?>) to).equals(Boolean.class)) {
                    return numberToBoolean(src);
                }
                if (to.equals(String.class)) {
                    return numberToString(src);
                }
            } else if (src instanceof String) {
                if (Primitives.wrap((Class<?>) to).equals(Boolean.class)) {
                    return stringToBoolean(src);
                }
                if (((Class<?>) to).isAssignableFrom(Number.class)) {
                    return stringToNumber(src);
                }
            } else if (src instanceof Boolean) {
                if (Primitives.wrap((Class<?>) to).equals(String.class)) {
                    return booleanToString(src);
                }
                if (((Class<?>) to).isAssignableFrom(Number.class)) {
                    return booleanToNumber(src);
                }
            }
        }
        return mGson.fromJson(mGson.toJson(src), to);
    }


    private static boolean numberToBoolean(Object src) {
        Number number = (Number) src;
        float value = number.floatValue();

        return value > 0;
    }

    private static String numberToString(Object src) {
        return String.valueOf(src);
    }

    private static Number booleanToNumber(Object src) {
        Boolean value = (Boolean) src;
        if (value) {
            return 1;
        } else {
            return 0;
        }
    }

    private static String booleanToString(Object src) {
        return String.valueOf(src);
    }

    private static Number stringToNumber(Object src) {
        try {
            return Float.valueOf(src.toString());
        } catch (NumberFormatException e) {
            LogUtil.debug(e);
        }

        return 0;
    }

    private static boolean stringToBoolean(Object src) {
        String str = src.toString();
        try {
            Float value = Float.valueOf(str);
            return value > 0;
        } catch (NumberFormatException e) {
            if (str.trim().equals("true")) {
                return true;
            } else {
                return false;
            }
        }

    }
}
