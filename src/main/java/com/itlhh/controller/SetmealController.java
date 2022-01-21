package com.itlhh.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itlhh.common.R;
import com.itlhh.dto.SetmealDto;
import com.itlhh.entity.Category;
import com.itlhh.entity.Setmeal;
import com.itlhh.service.CategoryService;
import com.itlhh.service.SetmealService;
import com.itlhh.vo.SetmealVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author lhh
 * @Date 2022/1/20 20:37
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询以及页面补全
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page=={},pageSize=={},name=={}", page, pageSize, name);

        //创建分页对象
        Page<Setmeal> pageInfo = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //创建分页条件---模糊查询(根据套餐名称)
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        //创建分页条件 --- 根据创建时间倒序排序
        queryWrapper.orderByDesc(Setmeal::getCreateTime);
        //执行分页查询
        setmealService.page(pageInfo, queryWrapper);


        Page<SetmealVo> res = new Page<>();

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealVo> records1 = new ArrayList<>();
        //遍历records,里面有套餐的记录
        for (Setmeal record : records) {
            //创建SetmealVo对象,开始补全
            SetmealVo setmealVo = new SetmealVo();
            setmealVo.setId(record.getId());
            setmealVo.setName(record.getName());
            setmealVo.setImage(record.getImage());
            setmealVo.setPrice(record.getPrice());
            setmealVo.setStatus(record.getStatus());
            setmealVo.setUpdateTime(record.getUpdateTime());

            //再创建一个分类补全 categoryName
            Category category = categoryService.getById(record.getCategoryId());
            //获取category这个对象,通过它获取名字
            String categoryName = category.getName();
            //将categoryName设置到setmealVo
            setmealVo.setCategoryName(categoryName);
            //最后将补全的全部添加到records1
            records1.add(setmealVo);
        }
        res.setRecords(records1);
        return R.success(res);
    }

    /**
     * 套餐新增
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String>  save(@RequestBody SetmealDto setmealDto){
        log.info("套餐新增的是:{}",setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 修改起售 停售状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R status(@PathVariable int status,Long[] ids){
        log.info("状态为:{},获取的ids是:{}",status,ids);

        //遍历ids
        for (Long id : ids) {
            //通过id获取对象
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);

            setmealService.updateById(setmeal);
        }

        return R.success("修改成功");
    }
}
