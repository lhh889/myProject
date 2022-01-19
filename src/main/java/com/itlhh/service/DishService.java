package com.itlhh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itlhh.entity.Dish;
import com.itlhh.entity.DishDto;

/**
 * @Author lhh
 * @Date 2022/1/17 17:20
 */
public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);
}
