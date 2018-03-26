package com.morse.mdao.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.morse.mdao.annotation.DbField;
import com.morse.mdao.annotation.DbTabel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    //创建一个缓存空间
    private HashMap<String, Field> cacheMap;

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

        cacheMap = new HashMap<>();
        isInit = true;
        return isInit;
    }

    private void initCacheMap() {
        String sql = "select * from " + tableName + " limit 1,0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        String[] columNames = cursor.getColumnNames();
        Field[] columFileds = entityClass.getDeclaredFields();
        for (Field field : columFileds) {
            field.setAccessible(true);
        }
        for (String columName : columNames) {
            Field columField = null;
            for (Field field : columFileds) {
                String fieldName = null;
                if (null != field.getAnnotation(DbField.class)) {
                    fieldName = field.getAnnotation(DbField.class).toString();
                } else {
                    fieldName = field.getName();
                }
                if (columName.equals(fieldName)) {
                    columField = field;
                    break;
                }
            }
            if (null != columField) {
                cacheMap.put(columName, columField);
            }
        }
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
        Map<String, String> map = getValues(entity);
        ContentValues contentValues = getContentValues(map);
        return sqLiteDatabase.insert(tableName, null, contentValues);
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (null != value) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }

    private Map<String, String> getValues(T entity) {
        Map<String, String> map = new HashMap<>();
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            field.setAccessible(true);
            try {
                Object object = field.get(entity);
                if (null == object) {
                    continue;
                }
                String values = object.toString();
                String key = null;
                if (null != field.getAnnotation(DbField.class)) {
                    key = field.getAnnotation(DbField.class).value();
                } else {
                    key = field.getName();
                }
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(values)) {
                    map.put(key, values);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
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
