package com.morse.mdao.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * 建表工厂
 * Created by morse on 2018/3/26.
 */

public class BaseDaoFactory {

    private static final BaseDaoFactory instance = new BaseDaoFactory();

    public static BaseDaoFactory getInstance() {
        return instance;
    }

    private SQLiteDatabase sqLiteDatabase;

    private String sqliteDatabasePath;

    private BaseDaoFactory() {
        sqliteDatabasePath = "data/data/android.database.sqlite.SQLiteDatabase/mdao.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath, null);
    }

    /**
     * 创建表并返回BaseDao对象
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> BaseDao<T> getBaseDao(Class<T> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return baseDao;
    }

}
