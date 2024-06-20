package com.dali.mybatisplus.controller;


import com.dali.mybatisplus.mapper.EmployeeMapper;
import com.dali.mybatisplus.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author:元哥说Java
 * V: szay2005
 * Q: 1794803734
 */
@RestController
public class EmpController {
    @Autowired
    EmployeeMapper employeeMapper;

    @GetMapping("/getEmp")
    public List<Employee> getEmp(){
        return employeeMapper.selectList(null);
    }
}