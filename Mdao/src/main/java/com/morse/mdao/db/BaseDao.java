package com.morse.mdao.db;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.morse.mdao.annotation.DbField;
import com.morse.mdao.annotation.DbTabel;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by morse on 2018/3/26.
 */
public class BaseDao<T> implements IBaseDao<T> {

    //持有数据库对象
    private SQLiteDatabase sqLiteDatabase;
    //表名
    private String tableName;
    //持有操作数据库的java类型
    private Class<T> entityClass;
    private boolean isInit = false;

    protected boolean init(SQLiteDatabase sqLiteDatabase, Class entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;

        if (isInit) {
            //自动建表


            //获取表名
            if (null == entityClass.getAnnotation(DbTabel.class)) {
                //通过反射获取类名
                tableName = entityClass.getSimpleName();
            } else {
                //获取注解上的名字
                tableName = entityClass.getAnnotation(DbTabel.class).toString();
                if (TextUtils.isEmpty(tableName)) {
                    tableName = entityClass.getSimpleName();
                }
            }
            if (!sqLiteDatabase.isOpen()) {
                return false;
            }
            //创建数据表
            //create table if not exisit tb_user(_id integer,name varchar(20),password varchar(20))
            String createTabel = getCreateTabelSql();
            sqLiteDatabase.execSQL(createTabel);
        }

        return isInit;
    }

    private String getCreateTabelSql() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("create tabel if not exists ");
        buffer.append(tableName + "(");
        //使用反射获取成员变量
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType();//获取到数据类型
            if (null != field.getAnnotation(DbField.class)) {
                if (type == String.class) {
                    buffer.append(field.getAnnotation(DbField.class).value() + " TEXT,");
                } else if (type == Integer.class) {
                    buffer.append(field.getAnnotation(DbField.class).value() + " INTEGER,");
                } else if (type == Long.class) {
                    buffer.append(field.getAnnotation(DbField.class).value() + " BIGINT,");
                } else if (type == Double.class) {
                    buffer.append(field.getAnnotation(DbField.class).value() + " DOUBLE,");
                } else if (type == byte[].class) {
                    buffer.append(field.getAnnotation(DbField.class).value() + " BLOB,");
                } else {
                    continue;
                }
            } else {
                if (type == String.class) {
                    buffer.append(field.getName() + " TEXT,");
                } else if (type == Integer.class) {
                    buffer.append(field.getName() + " INTEGER,");
                } else if (type == Long.class) {
                    buffer.append(field.getName() + " BIGINT,");
                } else if (type == Double.class) {
                    buffer.append(field.getName() + " DOUBLE,");
                } else if (type == byte[].class) {
                    buffer.append(field.getName() + " BLOB,");
                } else {
                    continue;
                }
            }
        }
        if (buffer.charAt(buffer.length() - 1) == ',') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public long insert(T entity) {
        return 0;
    }

    @Override
    public long update(T entity, T where) {
        return 0;
    }

    @Override
    public int delete(T where) {
        return 0;
    }

    @Override
    public List<T> query(T where) {
        return null;
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        return null;
    }

    @Override
    public List<T> query(String sql) {
        return null;
    }
}
