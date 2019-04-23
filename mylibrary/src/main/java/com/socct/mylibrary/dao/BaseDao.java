package com.socct.mylibrary.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.socct.mylibrary.listener.IResultListener;
import com.socct.mylibrary.LogTrackPool;
import com.socct.mylibrary.exception.LogTrackerException;
import com.socct.mylibrary.LogTrackerRunnable;
import com.socct.mylibrary.util.LogUtils;
import com.socct.mylibrary.util.ObjectHelper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建表和数据库的基本操作
 *
 * @author WJ
 * @date 19-4-19
 */
public abstract class BaseDao<M> implements IBaseDao<M> {


    private SQLiteDatabase mDatabase;
    private String mTbName;
    private Map<String, Field> mFieldMap;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isOpenTrack;

    /**
     * 初始化表操作对象
     *
     * @param database
     * @return
     */
    protected void init(SQLiteDatabase database, String dbName, Map<String, Field> fieldMap, boolean isOpenTrack) {
        this.mDatabase = database;
        this.mTbName = dbName;
        this.mFieldMap = fieldMap;
        this.isOpenTrack = isOpenTrack;
    }

    @Override
    public long insert(M entity) {
        checkOpenTrack();
        try {
            Map<String, String> values = getValues(entity);
            ContentValues cv = getContentValues(values);
            mDatabase.beginTransaction();
            long insert = mDatabase.insert(mTbName, null, cv);
            mDatabase.setTransactionSuccessful();
            String message = insert == -1 ? "插入数据失败" : "插入数据成功";
            LogUtils.e(message);
            return insert;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            mDatabase.setTransactionSuccessful();
            LogUtils.e("插入数据失败");
        } finally {
            mDatabase.endTransaction();
        }
        return -1L;
    }

    public void asyncInsert(final M entity) {
        asyncInsert(entity, null);
    }

    public void asyncInsert(final M entity, final IResultListener<Long> callback) {
        LogTrackPool.execute(new LogTrackerRunnable() {
            @Override
            public void doIt() {
                final long insert = insert(entity);
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.showResult(insert);
                        }
                    });
                }
            }
        });
    }


    /**
     * 删除数据
     *
     * @param where 　删除条件
     * @return 删除数据结果
     */
    @Override
    public int delete(M where) {
        checkOpenTrack();
        try {
            Map<String, String> whereMap = getValues(where);
            Condition condition = new Condition(whereMap);
            mDatabase.beginTransaction();
            int delete = mDatabase.delete(mTbName, condition.whereClause, condition.whereArgs);
            mDatabase.setTransactionSuccessful();
            LogUtils.e("删除成功");
            return delete;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            mDatabase.setTransactionSuccessful();
            LogUtils.e("删除失败");
        } finally {
            mDatabase.endTransaction();
        }
        return 0;
    }

    /**
     * 异步删除数据
     *
     * @param where 删除条件
     */
    public void asyncDelete(M where) {
        asyncDelete(where, null);
    }

    public void asyncDelete(final M where, final IResultListener<Integer> callback) {
        LogTrackPool.execute(new LogTrackerRunnable() {
            @Override
            public void doIt() {
                final int delete = delete(where);
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.showResult(delete);
                        }
                    });

                }
            }
        });
    }


    @Override
    public int update(M entity, M where) {
        checkOpenTrack();
        try {
            Map<String, String> values = getValues(entity);
            ContentValues cv = getContentValues(values);
            Map<String, String> whereMap = getValues(where);
            Condition condition = new Condition(whereMap);
            mDatabase.beginTransaction();
            int update = mDatabase.update(mTbName, cv, condition.whereClause, condition.whereArgs);
            mDatabase.setTransactionSuccessful();
            LogUtils.e("更新成功");
            return update;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            mDatabase.setTransactionSuccessful();
            LogUtils.e("更新失败");
        } finally {
            mDatabase.endTransaction();
        }

        return 0;
    }

    public void asyncUpdate(M entity, M where) {
        asyncUpdate(entity, where, null);
    }

    public void asyncUpdate(final M entity, final M where, final IResultListener<Integer> callback) {
        LogTrackPool.execute(new LogTrackerRunnable() {
            @Override
            public void doIt() {
                final int update = update(entity, where);
                if (callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.showResult(update);
                        }
                    });

                }
            }
        });
    }


    /**
     * @param where 　查询条件
     * @return 查询结果
     */
    @Override
    public List<M> query(M where) {
        return query(where, null);
    }

    public void asyncQuery(M where, IResultListener<List<M>> callback) {
        asyncQuery(where, null, callback);
    }

    /**
     * @param where   　查询条件
     * @param orderBy 　排序条件
     * @return 查询结果
     */
    @Override
    public List<M> query(M where, String orderBy) {
        return query(where, orderBy, null, null);
    }

    public void asyncQuery(M where, String orderBy, IResultListener<List<M>> callback) {
        asyncQuery(where, orderBy, null, null, callback);
    }


    /**
     * @param where     查询条件
     * @param orderBy   　排序条件
     * @param page      　　第几页
     * @param pageCount 　每页的个数
     * @return 查询结果
     */
    @Override
    public List<M> query(M where, String orderBy, Integer page, Integer pageCount) {
        checkOpenTrack();
        List<M> list = null;
        Cursor cursor = null;
        boolean isException = false;
        try {
            String limit = null;
            if (page != null && pageCount != null) {
                int startIndex = --page;
                limit = (startIndex < 0 ? 0 : startIndex) + "," + pageCount;
            }
            mDatabase.beginTransaction();
            if (where != null) {
                Map<String, String> whereMap = getValues(where);
                Condition condition = new Condition(whereMap);
                cursor = mDatabase.query(mTbName, null, condition.whereClause, condition.whereArgs, null, null, orderBy, limit);
            } else {
                cursor = mDatabase.query(mTbName, null, null, null, null, null, null, null);
            }
            list = getDataList(cursor);
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("查询失败");
            isException = true;
            mDatabase.setTransactionSuccessful();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            mDatabase.endTransaction();
        }
        if (!isException) {
            LogUtils.e("查询成功");
        }
        return null == list ? new ArrayList<M>() : list;
    }

    public void asyncQuery(final M where, final String orderBy, final Integer page, final Integer pageCount, final IResultListener<List<M>> callback) {
        LogTrackPool.execute(new LogTrackerRunnable() {
            @Override
            public void doIt() {
                ObjectHelper.requireNonNull(callback, "回调函数不可为空");
                final List<M> query = query(where, orderBy, page, pageCount);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.showResult(query);
                    }
                });

            }
        });
    }

    /**
     * 将对象中的属性转成键值对
     */
    private Map<String, String> getValues(M entity) throws IllegalAccessException {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Field> entry : mFieldMap.entrySet()) {
            Object value = entry.getValue().get(entity);
            result.put(entry.getKey(), value == null ? "" : value.toString());
        }
        return result;
    }

    /**
     * 将键值对转成ContentValues
     */
    private ContentValues getContentValues(Map<String, String> values) {
        ContentValues cv = new ContentValues();
        for (Map.Entry<String, String> val : values.entrySet()) {
            cv.put(val.getKey(), val.getValue());
        }
        return cv;
    }

    /**
     * 通过游标，将表中数据转成对象集合
     */
    private List<M> getDataList(Cursor cursor) throws IllegalAccessException, InstantiationException {
        if (cursor != null) {
            List<M> result = new ArrayList<>();
            /*为了保证游标的位置，先将它移动到最前面*/
            cursor.moveToFirst();
            /* 遍历游标，获取表中一行行的数据*/
            while (cursor.moveToNext()) {
                /*获取当前new的对象的 泛型的父类 类型*/
                ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
                /*获取第一个类型参数的真实类型*/
                Class<M> clazz = (Class<M>) pt.getActualTypeArguments()[0];
                /*创建对象*/
                M item = clazz.newInstance();
                /*遍历表字段，使用游标一个个取值，赋值给新创建的对象*/
                boolean isAdd = false;
                for (String columnName : mFieldMap.keySet()) {
                    /*找到表字段，找到表字段对应的类属性*/
                    Field field = mFieldMap.get(columnName);
                    /*   根据类属性类型，使用游标获取表中的值*/
                    Object val = null;
                    String name;
                    if (null == field) {
                        continue;
                    }
                    name = field.getType().getSimpleName();
                    switch (name) {
                        case "String":
                            val = cursor.getString(cursor.getColumnIndex(columnName));
                            break;
                        case "long":
                            val = cursor.getLong(cursor.getColumnIndex(columnName));
                            break;
                        case "int":
                            val = cursor.getInt(cursor.getColumnIndex(columnName));
                            break;
                        case "double":
                            val = cursor.getDouble(cursor.getColumnIndex(columnName));
                            break;
                        case "float":
                            val = cursor.getFloat(cursor.getColumnIndex(columnName));
                            break;
                    }
                    /*反射给对象属性赋值*/
                    field.set(item, val);
                    isAdd = true;
                }
                /*将对象添加到集合中*/
                if (isAdd) {
                    result.add(item);
                }
            }
            return result;
        }
        return null;
    }


    private void checkOpenTrack() {
        if (!isOpenTrack) {
            throw new LogTrackerException("你必须初始化LogTracker或者日志跟踪开光");
        }
    }

    /**
     * 　条件处理
     */
    class Condition {
        public Condition(Map<String, String> whereMap) {
            StringBuilder sb = new StringBuilder();
            List<String> list = new ArrayList<>();

            for (Map.Entry<String, String> entry : whereMap.entrySet()) {
                if (!TextUtils.isEmpty(entry.getValue())) {
                    sb.append("and ").append(entry.getKey()).append("=? ");
                    list.add(entry.getValue());
                }
            }
            this.whereClause = sb.delete(0, 4).toString();
            this.whereArgs = list.toArray(new String[0]);
        }

        String whereClause;
        String[] whereArgs;
    }
}
