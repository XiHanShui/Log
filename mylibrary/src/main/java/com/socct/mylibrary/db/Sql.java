package com.socct.mylibrary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.socct.mylibrary.exception.LogTrackerException;
import com.socct.mylibrary.util.LogUtils;
import com.socct.mylibrary.util.ObjectHelper;

import java.lang.reflect.Field;
import java.util.Map;

public class Sql extends SQLiteOpenHelper {


    private static Class<?>[] mClasses;

    private Map<String, String> mTableMap;

    private Map<String, Map<String, Field>> mFieldMap;

    public Sql(Context context, String dbName, Class<?>[] classes, int dbVersion) {
        super(context, dbName, null, dbVersion);
        ObjectHelper.requireNonNull(classes);
        mClasses = classes;
        int length = mClasses.length;
        createMap(length);
        addAllField();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Map.Entry<String, Map<String, Field>> entry : mFieldMap.entrySet()) {
            String key = entry.getKey();
            Map<String, Field> value = entry.getValue();
            String tableName = mTableMap.get(key);
            createTable(tableName, value, db);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private void addAllField() {
        for (Class<?> clazz : mClasses) {
            String mTbName = clazz.getSimpleName().toUpperCase();
            String name = clazz.getName();
            Map<String, Field> fieldMap = genFieldMap(clazz);
            if (fieldMap != null) {
                mFieldMap.put(name, fieldMap);
            }
            mTableMap.put(name, mTbName);
        }
    }

    private void createMap(int length) {
        if (null == mTableMap) {
            mTableMap = new ArrayMap<>(length);
        }
        if (null == mFieldMap) {
            mFieldMap = new ArrayMap<>(length);
        }
    }


    private void createTable(String mTbName, Map<String, Field> fieldMap, SQLiteDatabase db) {
        if (null == fieldMap) {
            throw new LogTrackerException("请检查实体类的成员变量");
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            String columnName = entry.getKey();
            Field field = entry.getValue();
            String type = null;
            String name = field.getType().getSimpleName();
            LogUtils.w(name);
            switch (name) {
                case "String":
                    type = "varchar";
                    break;
                case "long":
                    type = "long";
                    break;
                case "int":
                    type = "int";
                    break;
                case "double":
                    type = "double";
                    break;
                case "float":
                    type = "float";
                    break;
            }
            if (TextUtils.isEmpty(type)) {
                LogUtils.e(field.getName() + "是不支持的字段");
            } else {
                sb.append(columnName).append(" ").append(type).append(",");
            }
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        String s = sb.toString();
        if (TextUtils.isEmpty(s)) {
            LogUtils.e("获取不到表字段信息");
            return;
        }
        String sql = "create table if not exists " + mTbName + " (" + s + ") ";
        db.beginTransaction();
        db.execSQL(sql);
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    private Map<String, Field> genFieldMap(Class<?> clazz) {

        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 0) {
            LogUtils.e("获取不到类中字段");
            return null;
        }
        Map<String, Field> fieldMap = new ArrayMap<>(fields.length);
        for (Field field : fields) {
            field.setAccessible(true);
            String simpleName = field.getType().getSimpleName();
            if ("String".equals(simpleName) || "long".equals(simpleName) || "int".equals(simpleName)
                    || "double".equals(simpleName) || "float".equals(simpleName)) {
                fieldMap.put(field.getName().toUpperCase(), field);
            }
        }
        if (fieldMap.size() == 0) {
            LogUtils.e("获取不到类中字段");
            return null;
        }
        return fieldMap;
    }

    public String getTableName(Class<?> clazz) {
        String s = mTableMap.get(clazz.getName());
        ObjectHelper.requireNonNull(s, "表名错误");
        return s;
    }

    public Map<String, Field> getFieldMap(Class<?> clazz) {
        Map<String, Field> fieldMap = mFieldMap.get(clazz.getName());
        ObjectHelper.requireNonNull(fieldMap, "请检查表的成员属性");
        return fieldMap;
    }

}
