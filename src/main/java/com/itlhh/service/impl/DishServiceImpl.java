package com.itlhh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itlhh.entity.Dish;
import com.itlhh.dto.DishDto;
import com.itlhh.entity.DishFlavor;
import com.itlhh.exception.CustomException;
import com.itlhh.mapper.DishMapper;
import com.itlhh.service.DishFlavorService;
import com.itlhh.service.DishService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author lhh
 * @Date 2022/1/17 17:20
 */
@Service
//保证数据的一致性
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品信息
        this.save(dishDto);

        //获取保存的菜品id
        Long dishDtoId = dishDto.getId();

        //获取菜品口味列表,遍历列表,为菜品口味对象属性dishid赋值
        List<DishFlavor> flavors = dishDto.getFlavors();

        //System.out.println(flavors);
        //遍历列表
        for (DishFlavor flavor : flavors) {
            //为菜品口味对象属性dishid赋值
            flavor.setDishId(dishDtoId);
        }
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());

        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
       //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据 -- dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据 --- dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        //System.out.println(flavors);
        //遍历列表
        for (DishFlavor flavor : flavors) {
            //为菜品口味对象属性dishid赋值
            flavor.setDishId(dishDto.getId());
        }
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id删除菜品和菜品对应的关系表,同时判断菜品状态为起售不能删除
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询菜品状态，确定是否可用删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);

        int count = this.count(queryWrapper);
        if (count > 0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除菜品表中的数据-
        this.removeByIds(ids);

        LambdaQueryWrapper<DishFlavor> deletequeryWrapper = new LambdaQueryWrapper<>();
        deletequeryWrapper.in(DishFlavor::getDishId,ids);
        //删除关系表中的数据
        dishFlavorService.remove(deletequeryWrapper);
    }


}
