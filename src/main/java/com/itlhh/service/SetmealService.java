package com.itlhh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itlhh.dto.SetmealDto;
import com.itlhh.entity.Setmeal;

import java.util.List;

/**
 * @Author lhh
 * @Date 2022/1/17 17:21
 */
public interface SetmealService extends IService<Setmeal> {
    //新增套餐,同时插入套餐对应的菜品,要操作setmeal setmealdish表
    public void saveWithDish(SetmealDto setmealDto);

    //删除套餐和套餐的关系表id
    public void delWithSetmeal(List<Long> ids);

    //根据id查询套餐和套餐对应的关系表,回县数据
    public SetmealDto getWithId(Long id);
    //更新数据
    public void upWithSetmeal(SetmealDto setmealDto);
}
