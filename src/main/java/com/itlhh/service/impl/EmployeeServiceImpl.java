package com.itlhh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itlhh.entity.Employee;
import com.itlhh.mapper.EmployeeMapper;
import com.itlhh.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @Author lhh
 * @Date 2022/1/12 20:25
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
