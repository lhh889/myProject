package com.itlhh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itlhh.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author lhh
 * @Date 2022/1/12 20:22
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
