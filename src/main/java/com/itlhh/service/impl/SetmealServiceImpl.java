package com.itlhh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itlhh.dto.SetmealDto;
import com.itlhh.entity.Setmeal;
import com.itlhh.entity.SetmealDish;
import com.itlhh.mapper.SetmealMapper;
import com.itlhh.service.SetmealDishService;
import com.itlhh.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author lhh
 * @Date 2022/1/17 17:21
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐,同时插入套餐对应的菜品,要操作setmeal setmealdish表
     *
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //添加套餐
        this.save(setmealDto);


        Long setmealDtoId = setmealDto.getId();

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDtoId);
        }
        setmealDishService.saveBatch(setmealDishes);
    }
}
