package com.morse.mdao.db;

import java.util.List;

/**
 * Created by morse on 2018/3/26.
 */
public interface IBaseDao<T> {
    /**
     * 插入数据
     *
     * @param entity
     * @return
     */
    long insert(T entity);

    /**
     * 批量插入数据库
     *
     * @param entities
     * @return
     */
    long batchInsert(List<T> entities);

    /**
     * 更新数据
     *
     * @param entity
     * @param where
     * @return
     */
    long update(T entity, T where);

    /**
     * 删除数据
     *
     * @param where
     * @return
     */
    int delete(T where);

    /**
     * 查询数据
     *
     * @param where
     * @return
     */
    List<T> query(T where);

    /**
     * 查询数据
     *
     * @param where
     * @param orderBy
     * @param startIndex
     * @param limit
     * @return
     */
    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

    /**
     * 查询数据
     *
     * @param sql
     * @return
     */
    List<T> query(String sql, T where);

    /**
     * 清空表数据
     */
    void clear();
}
