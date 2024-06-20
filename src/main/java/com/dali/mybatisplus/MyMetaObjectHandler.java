package com.dali.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Author:元哥说Java
 * V: szay2005
 * Q: 1794803734
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入时：创建时间和修改时间
        this.setFieldValByName("createDate", new Date(), metaObject);
        this.setFieldValByName("modifyDate", new Date(), metaObject);

        //逻辑删除（0 未删除、1 删除）
        this.setFieldValByName("deleteFlag", 0, metaObject);

        //这里的“version”就是指定的字段，设置初始值为1，之后每修改一次自动+1
        this.setFieldValByName("version",1,metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("modifyDate", new Date(), metaObject);
    }
}
