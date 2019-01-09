package com.asniie.library.librarys;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.asniie.utils.sqlite.core.DataBase;
import com.asniie.utils.sqlite.core.InstanceProxy;
import com.asniie.utils.sqlite.exception.DataBaseException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by XiaoWei on 2019/1/9.
 */
public final class AndroidDb implements DataBase {
    private SQLiteDatabase database = null;
    private static InstanceProxy mProxy = new InstanceProxy(new AndroidDb());

    public static <T> T create(Class<T> clazz) {
        return mProxy.create(clazz);
    }

    @Override
    public boolean isOpen() {
        if (database != null)
            return database.isOpen();
        return false;
    }

    @Override
    public void close() {
        if (database != null)
            database.close();
    }

    @Override
    public int update(String sql) {
        try {
            database.execSQL(sql);
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    @Override
    public Object query(String sql) {
        Cursor cursor = database.rawQuery(sql, null);
        Map<String, Object> map = new HashMap<>();

        map.put("id", 3);
        map.put("name", "小明");
        map.put("age", 20);
        cursor.close();
        return map;
    }

    @Override
    public void connect(String path) throws DataBaseException {
        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new DataBaseException(e);
            }
        }
        database = SQLiteDatabase.openOrCreateDatabase(file.getAbsolutePath(), null);
    }
}
