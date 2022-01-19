package com.itlhh.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itlhh.common.R;
import com.itlhh.entity.Category;
import com.itlhh.entity.Dish;
import com.itlhh.entity.DishDto;
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
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if (dishDto!=null){
            return R.success(dishDto);
        }
        return R.error("没有该信息");
    }

    /**
     *修改数据
     * @param dishDto
     * @return
     */
    @PutMapping
   public R<String> update(@RequestBody DishDto dishDto){

            dishService.updateWithFlavor(dishDto);
            return R.success("修改成功");

    }

    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("ids====={}",ids);

        dishService.deleteWithFlavor(ids);

        return R.success("删除成功");
    }

    /**
     * 修改起售停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R statusA(@PathVariable int status,Long[] ids){
        log.info("ids==={}",ids);

        for (Long id : ids) {
            Dish byId = dishService.getById(id);

            byId.setStatus(status);

            dishService.updateById(byId);
        }

        return R.success("停售成功");
    }
}
