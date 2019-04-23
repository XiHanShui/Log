package com.socct.mylibrary.dao;

import java.util.List;

/**
 * 数据库操作规范
 *
 * @author WJ
 * @date 19-4-19
 */
public interface IBaseDao<M> {

    /**
     * 增
     *
     * @param entity 插入数据
     * @return 插入数据的id
     */
    long insert(M entity);


    /**
     * 删
     *
     * @param where
     * @return
     */
    int delete(M where);


    /**
     * 改
     *
     * @param entitiy
     * @param where
     * @return
     */
    int update(M entitiy, M where);

    /**
     * 查
     *
     * @param where 　查询条件
     * @return 全部数据
     */
    List<M> query(M where);

    /**
     * 查
     *
     * @param where   　查询条件
     * @param orderBy 　排序条件
     * @return 查询的数据
     */
    List<M> query(M where, String orderBy);

    /**
     * 查
     *
     * @param where     查询条件
     * @param orderBy   　排序条件
     * @param page      　　第几页
     * @param pageCount 　每页的个数
     * @return 查询出来的数据
     */
    List<M> query(M where, String orderBy, Integer page, Integer pageCount);

}
