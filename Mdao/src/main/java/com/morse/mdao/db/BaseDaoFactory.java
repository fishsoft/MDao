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
    public synchronized <T extends BaseDao<M>, M> T getBaseDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }

}
