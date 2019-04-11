package com.morse.mdao.db;

import android.content.Context;

/**
 * 数据库管理
 */
public class DBManager {

    private DBManager(Context context, Class<?>... clss) {
        if (null == context) {
            throw new NullPointerException("Context is null");
        }
        if (null == clss) {
            throw new NullPointerException("classEntries is null");
        }
        for (int i = 0; i < clss.length; i++) {
            BaseDaoFactory.getInstance(context).getBaseDao(clss[i]);
        }
    }

    /**
     * 获取Dao
     *
     * @param cls
     * @return
     */
    public static BaseDao getManagerDao(Class cls) {
        return BaseDaoFactory.getInstance().getBaseDao(cls);
    }

    public static class Builder {
        private Class<?>[] mClss;
        private Context mContext;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setClass(Class<?>... clss) {
            mClss = clss;
            return this;
        }

        public DBManager builder() {
            return new DBManager(mContext, mClss);
        }
    }
}
