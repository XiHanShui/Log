package com.socct.mylibrary.dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.ArrayMap;

import com.socct.mylibrary.LogTrackerConfig;
import com.socct.mylibrary.db.Sql;


/**
 * @author WJ
 * @date 19-4-19
 */
public final class LogDaoFactory {

    private static String mDbName;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static boolean isOpenTrack;
    private SQLiteDatabase mDatabase;
    private static Class<?>[] mClasses;
    private static int mDbVersion;
    private Sql mSql;
    private final ArrayMap<String, BaseDao> mDaoMap;


    public static LogDaoFactory getInstance() {
        return Instance.INSTANCE;
    }


    private LogDaoFactory() {
        mDaoMap = new ArrayMap<>(mClasses.length);
    }


    // 初始化数据库位置
    public static void init(Class<?>[] classes, LogTrackerConfig config) {
        mDbName = config.mDbName;
        mContext = config.mContext;
        mClasses = classes;
        mDbVersion = config.dbVersion;
        isOpenTrack = config.isOpenTrack;
    }


    public <T extends BaseDao<M>, M> T getDataHelper(Class<T> clazz, Class<M> entity) {
        if (null == mDatabase) {
            mSql = new Sql(mContext, mDbName, mClasses, mDbVersion);
            mDatabase = mSql.getWritableDatabase();
        }
        T baseDao = null;
        try {
            String name = entity.getName();
            baseDao = (T) mDaoMap.get(name);
            if (null == baseDao) {
                baseDao = clazz.newInstance();
                baseDao.init(mDatabase, mSql.getTableName(entity), mSql.getFieldMap(entity), isOpenTrack);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return baseDao;
    }

    private final static class Instance {
        @SuppressLint("StaticFieldLeak")
        private final static LogDaoFactory INSTANCE = new LogDaoFactory();
    }
}
