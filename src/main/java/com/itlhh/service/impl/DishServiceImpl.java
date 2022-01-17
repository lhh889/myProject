package com.itlhh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itlhh.entity.Dish;
import com.itlhh.mapper.DishMapper;
import com.itlhh.service.DishService;
import org.springframework.stereotype.Service;

/**
 * @Author lhh
 * @Date 2022/1/17 17:20
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
