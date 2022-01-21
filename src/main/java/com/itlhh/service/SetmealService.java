package com.itlhh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itlhh.dto.SetmealDto;
import com.itlhh.entity.Setmeal;

/**
 * @Author lhh
 * @Date 2022/1/17 17:21
 */
public interface SetmealService extends IService<Setmeal> {
    //新增套餐,同时插入套餐对应的菜品,要操作setmeal setmealdish表
    public void saveWithDish(SetmealDto setmealDto);
}
