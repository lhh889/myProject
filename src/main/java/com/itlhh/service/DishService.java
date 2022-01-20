package com.itlhh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itlhh.entity.Dish;
import com.itlhh.dto.DishDto;

import java.util.List;

/**
 * @Author lhh
 * @Date 2022/1/17 17:20
 */
public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新对应的口味信息
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品，同时需要删除菜品和菜品的关联数据
    public void removeWithDish(List<Long> ids);
}
