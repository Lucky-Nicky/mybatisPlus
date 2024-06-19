package com.dali.mybatisplus;

import com.dali.mybatisplus.model.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmployeeDataGenerator {

    public static String[] generateEmployeeData1() {
        // 获取当前时间的时间戳
        long timestamp = System.currentTimeMillis();

        // 生成时间戳的后8位
        String last8Digits = Long.toString(timestamp).substring(Long.toString(timestamp).length() - 8);

        // 生成邮箱
        String email = last8Digits + "@qq.com";

        // 随机生成性别，"0"代表女性，"1"代表男性
        String gender = new Random().nextBoolean() ? "1" : "0";

        // 使用时间截的最后两位来模拟年龄，保证年龄在 [0, 99] 范围内
        int age = (int) (timestamp % 100);

        return new String[]{last8Digits, email, gender, String.valueOf(age)};
    }
    public static Employee generateEmployeeData() {
        // 获取当前时间的时间戳
        long timestamp = System.currentTimeMillis();

        // 生成时间戳的后8位
        String last8Digits = Long.toString(timestamp).substring(Long.toString(timestamp).length() - 8);

        // 生成邮箱
        String email = last8Digits + "@qq.com";

        // 随机生成性别，"0"代表女性，"1"代表男性
        int gender = Integer.parseInt(new Random().nextBoolean() ? "1" : "0");

        // 使用时间截的最后两位来模拟年龄，保证年龄在 [0, 99] 范围内
        int age = (int) (timestamp % 100);

        return new Employee(last8Digits, email, gender, age);
    }
}

