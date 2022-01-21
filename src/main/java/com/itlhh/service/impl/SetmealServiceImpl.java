package com.itlhh.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itlhh.dto.SetmealDto;
import com.itlhh.entity.Setmeal;
import com.itlhh.entity.SetmealDish;
import com.itlhh.exception.CustomException;
import com.itlhh.mapper.SetmealMapper;
import com.itlhh.service.SetmealDishService;
import com.itlhh.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @Author lhh
 * @Date 2022/1/17 17:21
 */
@Service
//事务注解
@Transactional
@Slf4j
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

    /**
     * 删除套餐和套餐的关系表id
     *
     * @param ids
     */
    @Override
    public void delWithSetmeal(List<Long> ids) {
        //判断是否停售起售
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //判断是否包含
        queryWrapper.in(Setmeal::getId, ids);
        //是否是起售状态 起售:1  停售:0
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        //count有数据,不能删除,抛异常
        if (count > 0) {
            log.info("--->count:{}", count);
            throw new CustomException("该套餐正在售卖中,请勿删除");
        }
        //停售状态正常删除
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> delqueryWrapper = new LambdaQueryWrapper<>();
        delqueryWrapper.in(SetmealDish::getSetmealId, ids);
        //删除套餐对应的关系表
        setmealDishService.remove(delqueryWrapper);
    }

    /**
     * 根据id查询套餐和套餐对应的关系表,回显数据
     *
     * @param id
     * @return
     */
    @Override
    public SetmealDto getWithId(Long id) {
        //根据id查询套餐
        Setmeal setmeal = this.getById(id);
        log.info("setmeal========={}", setmeal);
        //获取套餐的id
        Long setmealId = setmeal.getId();
        //创建setmealDto对象
        SetmealDto setmealDto = new SetmealDto();
        //log.info("======>setmealDto===>:{}",setmealDto);
        //将setmeal复制到setmealDto
        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, id);
        //查询关系表的信息
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        //遍历关系表信息
        for (SetmealDish setmealDish : setmealDishList) {
            //把套餐的id设置到套餐关系表里面
            setmealDish.setSetmealId(setmealId);
        }
        setmealDto.setSetmealDishes(setmealDishList);
        //将封装的信息返回
        return setmealDto;
    }

    @Override
    public void upWithSetmeal(SetmealDto setmealDto) {
        //更新套餐表
        this.updateById(setmealDto);

        //更新对应关系表前要先删除,后重新添加(个人理解是因为里面的数据和修改不一样,不知道如何更改,还不如直接删除重新添加)
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //删除完重新添加
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishList);
    }
}
