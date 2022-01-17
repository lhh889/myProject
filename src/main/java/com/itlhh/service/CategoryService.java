package com.itlhh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itlhh.entity.Category;


/**
 * @Author lhh
 * @Date 2022/1/17 16:44
 */
public interface CategoryService extends IService<Category> {

    //根据id删除分类
    public void remove(Long id);

}
