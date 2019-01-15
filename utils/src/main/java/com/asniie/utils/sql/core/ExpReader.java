package com.asniie.utils.sql.core;

/*
 * Created by XiaoWei on 2019/1/13.
 */

import com.asniie.utils.sql.exception.ExpParseException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class ExpReader extends StringReader {

    private StringBuilder mBuilder = new StringBuilder();
    private int level = 0;
    private boolean isExp = false;

    public ExpReader(String src) {
        super(src);
    }

    private String value() {
        String str = mBuilder.toString();
        mBuilder.delete(0, mBuilder.length());
        return str.trim().length() > 0 ? str : null;
    }

    public String[] peek() {
        List<String> mList = new ArrayList<>(10);
        try {
            int buf;
            while ((buf = read()) != -1) {
                char ch = (char) buf;
                switch (buf) {
                    case '$':
                        mark(0);
                        if (read() == '{') {
                            isExp = true;
                            level++;
                        }
                        mBuilder.append(ch);
                        reset();
                    break;
                    case '.':
                        if (isExp) {
                            mBuilder.append(ch);
                        } else {
                            String value = value();
                            if (value != null) {
                                mList.add(value);
                            }
                        }
                        break;
                    case '}':
                        level--;
                        isExp = level != 0;
                        mBuilder.append(ch);
                        if (!isExp) {
                            String value = value();
                            if (value != null) {
                                mList.add(value);
                            }
                        }
                        break;
                    default:
                        mBuilder.append(ch);
                        break;
                }
            }

            String value = value();
            if (value != null) {
                mList.add(value);
            }
        } catch (IOException e) {
            throw new ExpParseException(e);
        }

        return mList.toArray(new String[]{});
    }
}

