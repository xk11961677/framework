package com.sky.framework.web.mybatis;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;


/**
 * The interface My mapper.
 * @author
 */
@SuppressWarnings("unused")
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
