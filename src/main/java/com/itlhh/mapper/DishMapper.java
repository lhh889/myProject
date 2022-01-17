package com.itlhh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itlhh.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author lhh
 * @Date 2022/1/17 17:18
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
