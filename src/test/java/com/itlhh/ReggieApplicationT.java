package com.itlhh;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itlhh.common.R;
import com.itlhh.entity.Employee;
import com.itlhh.mapper.EmployeeMapper;
import com.itlhh.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author lhh
 * @Date 2022/1/12 19:06
 */
@SpringBootTest
public class ReggieApplicationT {


    @Autowired
    private EmployeeService employeeService;
    @Test

    public void test(){
        List<Employee> list = employeeService.list();
        System.out.println(list);
    }
}
