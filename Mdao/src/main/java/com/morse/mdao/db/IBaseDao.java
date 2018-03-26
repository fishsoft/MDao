package com.morse.mdao.db;

import java.util.List;

/**
 * Created by morse on 2018/3/26.
 */

public interface IBaseDao<T> {
    long insert(T entity);

    long update(T entity, T where);

    int delete(T where);

    List<T> query(T where);

    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

    List<T> query(String sql);
}
