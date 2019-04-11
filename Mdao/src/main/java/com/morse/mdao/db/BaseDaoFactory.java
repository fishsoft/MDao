package com.morse.mdao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * 建表工厂
 * Created by morse on 2018/3/26.
 */
public class BaseDaoFactory {

    private static BaseDaoFactory instance;

    public static BaseDaoFactory getInstance(Context context) {
        if (null == instance) {
            synchronized (BaseDaoFactory.class) {
                if (null == instance) {
                    instance = new BaseDaoFactory(context);
                }
            }
        }
        return instance;
    }

    public static BaseDaoFactory getInstance() {
        return instance;
    }

    private SQLiteDatabase sqLiteDatabase;

    private BaseDaoFactory(Context context) {
        String packageName = context.getPackageName();
        String sqliteDatabasePath = "data/data/" + packageName + "/data.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath, null);
    }

    /**
     * 指定路径生成数据库
     * <p>可以保存到任意位置</p>
     *
     * @param path 绝对路径
     */
    private BaseDaoFactory(String path) {
        try {
            if (TextUtils.isEmpty(path)) {
                throw new NullPointerException("path is null");
            }
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(file, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建表并返回BaseDao对象
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public synchronized <T extends BaseDao<M>, M> T getBaseDao(Class<M> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }

}
