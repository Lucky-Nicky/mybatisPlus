package com.dali.mybatisplus;

import com.baomidou.mybatisplus.core.mapper.Mapper;

import java.io.Serializable;

public interface BaseMapper<T> extends Mapper {
    int insert(T entity);
    int deleteById(Serializable id);
    int deleteByID(T entity);
}
