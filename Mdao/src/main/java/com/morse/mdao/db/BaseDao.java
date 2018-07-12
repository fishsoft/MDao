package com.morse.mdao.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.morse.mdao.annotation.DbField;
import com.morse.mdao.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
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

    protected boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;

        if (!isInit) {
            //自动建表

            //获取表名
            if (null == entityClass.getAnnotation(DbTable.class)) {
                //通过反射获取类名
                tableName = entityClass.getSimpleName();
            } else {
                //获取注解上的名字
                tableName = entityClass.getAnnotation(DbTable.class).value();
                if (TextUtils.isEmpty(tableName)) {
                    tableName = entityClass.getSimpleName();
                }
            }
            if (!sqLiteDatabase.isOpen()) {
                return false;
            }
            //创建数据表
            //create table if not exisit tb_user(_id integer,name varchar(20),password varchar(20))
            String createTabel = getCreateTableSql();
            sqLiteDatabase.execSQL(createTabel);
            cacheMap = new HashMap<>();
            initCacheMap();
        }

        isInit = true;
        return isInit;
    }

    /**
     * 缓存数据库字段
     */
    private void initCacheMap() {
        String sql = "select * from " + tableName + " limit 1,0";//获取的是空表，只有表的字段名
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        String[] columNames = cursor.getColumnNames();//获取表字段
        Field[] columFileds = entityClass.getDeclaredFields();//获取对应的类中的字段
        for (Field field : columFileds) {
            field.setAccessible(true);//将字段的访问类型为可访问
        }

        //数据表中的列名与对应的类中的数据进行对比，如果是一样的，就将数据保存到缓存中
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

    private String getCreateTableSql() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("create table if not exists ");
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

    /**
     * 将数据填充到ContentValues中
     *
     * @param map
     * @return
     */
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

    /**
     * entity与数据库的映射
     *
     * @param entity
     * @return
     */
    private Map<String, String> getValues(T entity) {
        Map<String, String> map = new HashMap<>();
        Iterator<Field> fieldIterator = cacheMap.values().iterator();//获取数据库字段
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            field.setAccessible(true);
            try {
                Object object = field.get(entity);//从entity中读取值
                if (null == object) {
                    continue;
                }
                String values = object.toString();//将值转成字符串
                String key = null;//获取entity中的值对应的数据库字段
                if (null != field.getAnnotation(DbField.class)) {
                    key = field.getAnnotation(DbField.class).value();//有注解
                } else {
                    key = field.getName();//没注解
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

    static class Condition {
        private String whereCasue;
        private String[] whereArgs;

        public Condition(Map<String, String> whereCasue) {
            List list = new ArrayList();
            StringBuffer buffer = new StringBuffer();
            buffer.append("1=1");
            Set<String> keys = whereCasue.keySet();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = whereCasue.get(key);
                if (null != value) {
                    buffer.append(" and " + key + "=?");
                    list.add(value);
                }
            }
            this.whereCasue = buffer.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);
        }
    }

    @Override
    public long update(T entity, T where) {
        int result = -1;
        Map value = getValues(entity);
        ContentValues contentValues = getContentValues(value);
        Map whereCase = getValues(where);
        Condition condition = new Condition(whereCase);
        result = sqLiteDatabase.update(tableName, contentValues, condition.whereCasue, condition.whereArgs);
        return 0;
    }

    @Override
    public int delete(T where) {
        Map map = getValues(where);
        Condition condition = new Condition(map);
        int result = sqLiteDatabase.delete(tableName, condition.whereCasue, condition.whereArgs);
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {

//        sqLiteDatabase.query(tableName,null,"is=?",new String[],null,null,orderBy)
        Map map = getValues(where);
        String limitString = null;
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(map);
        Cursor cursor = sqLiteDatabase.query(tableName, null, condition.whereCasue, condition.whereArgs, null, null, orderBy, limitString);
        List<T> result = getResult(cursor, where);
        return result;
    }

    private List<T> getResult(Cursor cursor, T where) {
        ArrayList list = new ArrayList<>();
        Object item = null;
        while (cursor.moveToNext()) {
            try {
                item = where.getClass().newInstance();
                Iterator it = cacheMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    //取数据库列名
                    String column = (String) entry.getKey();
                    //去列名所在游标位置
                    int index = cursor.getColumnIndex(column);
                    Field field = (Field) entry.getValue();
                    Class type = field.getType();
                    if (-1 != index) {
                        if (String.class == type) {
                            field.set(item, cursor.getString(index));
                        } else if (Double.class == type) {
                            field.set(item, cursor.getDouble(index));
                        } else if (Long.class == type) {
                            field.set(item, cursor.getLong(index));
                        } else if (byte[].class == type) {
                            field.set(item, cursor.getBlob(index));
                        } else {
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    @Override
    public List<T> query(String sql) {
//        List<T> list=sqLiteDatabase.execSQL(sql);
        return null;
    }
}
