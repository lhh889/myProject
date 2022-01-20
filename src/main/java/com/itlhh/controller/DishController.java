package com.itlhh.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itlhh.common.R;
import com.itlhh.entity.Category;
import com.itlhh.entity.Dish;
import com.itlhh.dto.DishDto;
import com.itlhh.service.CategoryService;
import com.itlhh.service.DishService;
import com.itlhh.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lhh
 * @Date 2022/1/19 9:13
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增成功");
    }

    /**
     * 分页查询以及进行页面补全
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //log.info("page=={},pageSize =={},name=={}",page,pageSize,name);

        //创建分页对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询,按照菜品名称
        queryWrapper.like(!StringUtils.isEmpty(name), Dish::getName, name);

        //按照创建时间倒序排序
        queryWrapper.orderByDesc(Dish::getCreateTime);
        dishService.page(pageInfo, queryWrapper);
        //返回一个Page<DishVo> 我需要对res进行补全
        Page<DishVO> res = new Page<>();
        //先对页面结果进行补全
        List<Dish> records = pageInfo.getRecords();//获取dish的浏览器信息

        List<DishVO> resRecords = new ArrayList<>();

        res.setTotal(pageInfo.getTotal());
        res.setCountId(pageInfo.getCountId());

        for (Dish record : records) {
            //开始补全
            DishVO dishVO = new DishVO();
            dishVO.setId(record.getId());
            dishVO.setName(record.getName());
            dishVO.setImage(record.getImage());
            dishVO.setPrice(record.getPrice());
            dishVO.setStatus(record.getStatus());
            dishVO.setUpdateTime(record.getUpdateTime());

            //还需要补全一个分类名称
            Category category = categoryService.getById(record.getCategoryId());
            dishVO.setCategoryName(category.getName());
            //至此补全完成，写入到resRecords
            resRecords.add(dishVO);
        }
        res.setRecords(resRecords);
        return R.success(res);
    }

    /**
     * 根据id查询数据,回显数据
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if (dishDto != null) {
            return R.success(dishDto);
        }
        return R.error("没有该信息");
    }

    /**
     * 修改数据
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");

    }

    /**
     * 批量删除 单个删除 同时判断是否是起售状态,起售就禁止删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids====={}", ids);

        dishService.removeWithDish(ids);

        return R.success("删除成功");
    }

    /**
     * 修改起售停售
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> statusA(@PathVariable int status, Long[] ids) {
        log.info("ids==={}", ids);
        //获取到前端传来的id 遍历ids
        for (Long id : ids) {
            //通过id获取到dish对象
            Dish dish = dishService.getById(id);
            //将前端获取的status状态设置到dish里面
            dish.setStatus(status);

            dishService.updateById(dish);
        }

        return R.success("停售成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据菜品分类categoryid进行查询
        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        //限定菜品的的状态为起售状态
        queryWrapper.eq(Dish::getStatus, 1);
        //最后对查询的结果进行排序(sort升序排序,sort相同进行修改时间倒序排序)
        queryWrapper.orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);

        return R.success(dishList);
    }
}
