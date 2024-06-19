package com.dali.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dali.mybatisplus.model.Employee;

/**
 * Author:元哥说Java
 * V: szay2005
 * Q: 1794803734
 * BaseMapper<T> : 泛型指定的就是当前Mapper接口所操作的实体类类型
 */
public interface EmployeeMapper extends BaseMapper<Employee> {
}
