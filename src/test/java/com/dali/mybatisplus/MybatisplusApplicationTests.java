package com.dali.mybatisplus;

import com.baomidou.mybatisplus.core.toolkit.AES;
import com.dali.mybatisplus.mapper.EmployeeMapper;
import com.dali.mybatisplus.model.Employee;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.dali.mybatisplus.EmployeeDataGenerator.*;

@Nested
@SpringBootTest(args = "--mpw.key=cd562e25eec0e0a2")
class MybatisplusApplicationTests {

    @Autowired
    EmployeeMapper employeeMapper;

    @Test
    public void testInsert() {
        // 获取当前时间的时间戳
        long timestamp = System.currentTimeMillis();

        // 生成时间戳的后8位
        String timestampStr = String.valueOf(timestamp);
        int length = timestampStr.length();
        // 确保字符串长度至少为8，避免越界
        int beginIndex = Math.max(0, length - 8);
        String last8Digits = timestampStr.substring(beginIndex);

        // 生成邮箱
        String email = last8Digits + "@qq.com";

        // 随机生成性别
        Random random = new Random();
        Integer gender = random.nextBoolean() ? 1 : 0;

        // 使用时间戳的最后两位
        int age = Integer.parseInt(String.valueOf(timestamp).substring(11, 13));
        Employee employee = new Employee(last8Digits, email, gender, age);

        int insert = employeeMapper.insert(employee);
        Integer id = employee.getId();//此方法可以获取插入当前记录在数据库中的id
        System.out.println("当前插入数据的id=====" + id);
        if (id != null) {
            // 使用 ID 查询数据
            Employee employee2 = employeeMapper.selectById(id);
            if (employee2 != null) {
                System.out.println("查询到的数据: " + employee2);
            } else {
                System.out.println("未查询到数据");
            }
        } else {
            System.out.println("插入数据未生成 ID");
        }
    }

    // 批量查询方法SelectBatch
    @Test
    public void testSelectBatch() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        /**
         * 发现该方法底层使用的竟然是sql的in关键字
         * SELECT
         *   *
         * FROM
         *     tbl_employee
         * WHERE
         *     id IN (
         *         1 , 2 , 3
         *     )
         * */
        List<Employee> employees = employeeMapper.selectBatchIds(list);
        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }

    // selectByMap方法
    @Test
    public void testSelectByMap() {
        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("last_name", "zhangsan");
        map.put("age", 22);
        /**
         * selectByMap的key是数据库的字段名
         * SELECT
         *     id,
         *     last_name,
         *     email,
         *     gender,
         *     age
         * FROM
         *     tbl_employee
         * WHERE
         *     last_name = 'zhangsan'
         *     AND age = 22
         *
         * */
        List<Employee> employees = employeeMapper.selectByMap(map);
        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }

    // 删除方法
    @Test
    public void testDelete() {

        //根据ID删除
        employeeMapper.deleteById(1);

        //根据条件删除
        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("last_name", "zhangsan");
        map.put("age", 22);
        employeeMapper.deleteByMap(map);
    }

    // 自动填充功能
    @Test
    public void testFieldFill() {
        /**
         * INSERT
         * INTO
         *     tbl_employee
         *     ( last_name, email, gender, age, create_date, modify_date )
         * VALUES
         *     ( 'lisi', 'lisi@hello.com', 1, 20, '2022-11-15 00:59:40.645', '2022-11-15 00:59:40.645' )
         *
         * */
        String[] employeeData = generateEmployeeData1();
        String last8Digits = employeeData[0];
        String email = employeeData[1];
        int gender = Integer.parseInt(employeeData[2]);
        int age = Integer.parseInt(employeeData[3]);
        Employee employee = new Employee(last8Digits, email, gender, age);
        int insert = employeeMapper.insert(employee);
    }

    @Test
    public void testFieldFill2() {
        /**
         * INSERT
         * INTO
         *     tbl_employee
         *     ( last_name, email, gender, age, create_date, modify_date )
         * VALUES
         *     ( 'lisi', 'lisi@hello.com', 1, 20, '2022-11-15 00:59:40.645', '2022-11-15 00:59:40.645' )
         *
         * */
        Employee employee = generateEmployeeData();
        int insert = employeeMapper.insert(employee);

    }

    // 逻辑删除  --- update
    @Test
    public void testDelete2() {

        /**
         * UPDATE
         *     tbl_employee
         * SET
         *     delete_flag=1
         * WHERE
         *     id=2
         *     AND delete_flag=0
         * */

        employeeMapper.deleteById(2);
    }

    // 执行查询会带上 delete_flag=0
    @Test
    public void testList() {
        /**
         * SELECT *
         * FROM
         *     tbl_employee
         * WHERE
         *     delete_flag=0
         *
         * */
        List<Employee> employees = employeeMapper.selectList(null);
    }
    //丢失更新问题
    @Test
    void testCAS2(){

        // 线程1  age:50  version:1
        Employee employee1 = employeeMapper.selectById(35);

        // 线程2 age:50  version:1
        Employee employee2 = employeeMapper.selectById(35);

        employee1.setAge(100);

        employee2.setAge(80);

        if(employeeMapper.updateById(employee1) > 0){
            System.out.println("更新成功");
        }
        if(employeeMapper.updateById(employee2) == 0){
            System.out.println("更新失败，请重新刷新页面并更新");
        }
    }
    //引入乐观锁解决了并发更新问题
    @Test
    void testCAS(){

        // 线程1  age:50  version:1
        Employee employee1 = employeeMapper.selectById(36);

        // 线程2 age:50  version:1
        Employee employee2 = employeeMapper.selectById(36);

        employee1.setAge(100);

        employee2.setAge(80);

        /**
         * update age 100  version:2  where  version1 =数据库version1
         *
         * UPDATE
         *     tbl_employee
         * SET
         *     last_name='C',
         *     email='123@qq.com',
         *     gender=1,
         *     age=100,
         *     modify_date='2022-11-15 07:09:31.162',
         *     version=2
         * WHERE
         *     id=2
         *     AND version=1
         *     AND delete_flag=0
         */
        if(employeeMapper.updateById(employee1) > 0){
            System.out.println("更新成功");
        }

        /**
         * update 80  version:2 where version1 =数据库version2
         *UPDATE
         *     tbl_employee
         * SET
         *     last_name='C',
         *     email='123@qq.com',
         *     gender=1,
         *     age=80,
         *     modify_date='2022-11-15 07:09:31.18',
         *     version=2
         * WHERE
         *     id=2
         *     AND version=1
         *     AND delete_flag=0
         * */
        if(employeeMapper.updateById(employee2) == 0){
            System.out.println("更新失败，请重新刷新页面并更新");
        }
    }
    @Test
    void testRandomKey(){// 生成 16 位随机 AES 密钥
        String randomKey = AES.generateRandomKey();
        System.out.println("这是新生成的秘钥++++++++++++++++++"+randomKey);
        //fed64f56abe56bd6
    }

    @Test
    void testEncrypt(){
        String url = AES.encrypt("jdbc:mysql://124.223.59.152:3306/mybatis?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC" , "cd562e25eec0e0a2");
        String uname = AES.encrypt("root" , "cd562e25eec0e0a2");
        String pwd = AES.encrypt("19941229Ddl" , "cd562e25eec0e0a2");

        System.out.println("这是新生成的加密url++++++++++++++++++"+url);
        // x22Gia7StPlQXDHL4weOZe92WQ9Kj20/Da/M3rNYBVdYcdhOl+yOGs/JrFcwAW9FTFxiSMa1fe6Mwd2jH+MPP30u8mJw9nFOiNpNsNlX0t0WE8ohrkZOWVN4WfUWAnTZgzNS4VHbr3YtuPGbtc4NGw==
        System.out.println("这是新生成的加密uname++++++++++++++++++"+uname);
        // uWw+rfWZ9jOnv02UGqwung==
        System.out.println("这是新生成的加密pwd++++++++++++++++++"+pwd);
        // rYg5K6hkfGo8hoGT5n+DKg==
    }
}
