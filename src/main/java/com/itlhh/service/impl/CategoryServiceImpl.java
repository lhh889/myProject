package com.itlhh.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itlhh.entity.Category;
import com.itlhh.entity.Dish;
import com.itlhh.entity.Setmeal;
import com.itlhh.exception.CustomException;
import com.itlhh.mapper.CategoryMapper;
import com.itlhh.service.CategoryService;
import com.itlhh.service.DishService;
import com.itlhh.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author lhh
 * @Date 2022/1/17 16:45
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;//菜品

    @Autowired
    private SetmealService setmealService;//套餐

    @Override
    public void remove(Long id) {
        //添加查询条件，根据分类id进行查询菜品数据
        LambdaQueryWrapper<Dish> dishquerWrapper = new LambdaQueryWrapper<>();
        dishquerWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishquerWrapper);

        //判断count是否有菜品 >0有关联菜品抛出异常
        if (count1 > 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }


        LambdaQueryWrapper<Setmeal> setmealquerWrapper = new LambdaQueryWrapper<>();
        setmealquerWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealquerWrapper);

        if (count2 > 0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //没有关联正常删除
        super.removeById(id);
    }



}
