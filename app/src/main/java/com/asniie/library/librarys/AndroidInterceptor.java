package com.asniie.library.librarys;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.asniie.utils.sqlite.Interceptor.AbstractInterceptor;
import com.asniie.utils.sqlite.exception.DataBaseException;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by XiaoWei on 2019/1/10.
 */
public final class AndroidInterceptor extends AbstractInterceptor {
    private final Gson mGson = new Gson();

    @Override
    public Object intercept(String[] sqls, ExecType type, Type returnType) {
        SQLiteDatabase database = connect("database.db");

        Object object = null;

        if (type == ExecType.QUERY) {
            for (String sql : sqls) {
                if (sql != null) {
                    object = query(database, sql, returnType);
                }
            }
        } else {
            int count = 0;

            database.beginTransaction();
            for (String sql : sqls) {
                if (sql != null) {
                    int code = update(database, sql);
                    if (code == 0) {
                        count = 0;
                        break;
                    } else {
                        count += code;
                    }
                }
            }
            if (count != 0) {
                database.setTransactionSuccessful();
            }
            database.endTransaction();

            if (returnType.equals(boolean.class) || returnType.equals(Boolean.class)) {
                object = (count != 0);
            } else {
                object = count;
            }
        }

        if (database != null && database.isOpen()) {
            database.close();
        }

        return object;
    }

    private int update(SQLiteDatabase database, String sql) {
        try {
            database.execSQL(sql);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    private Object query(SQLiteDatabase database, String sql, Type returnType) {

        Cursor cursor = database.rawQuery(sql, null);

        List<Map<String, Object>> array = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Map<String, Object> map = new HashMap<>();

            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                int type = cursor.getType(i);
                String key = cursor.getColumnName(i);

                switch (type) {
                    case Cursor.FIELD_TYPE_STRING:
                        map.put(key, cursor.getString(i));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        map.put(key, cursor.getInt(i));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        map.put(key, cursor.getFloat(i));
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        map.put(key, null);
                        break;
                    case Cursor.FIELD_TYPE_BLOB:

                        break;
                }
            }

            array.add(map);
            cursor.moveToNext();
        }

        cursor.close();
        return mGson.fromJson(mGson.toJson(array), returnType);
    }

    private SQLiteDatabase connect(String path) throws DataBaseException {
        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new DataBaseException(e);
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(file.getAbsolutePath(), null);
    }
}
