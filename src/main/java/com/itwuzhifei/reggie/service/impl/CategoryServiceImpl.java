package com.itwuzhifei.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itwuzhifei.reggie.common.CustomerException;
import com.itwuzhifei.reggie.mapper.CategoryMapper;
import com.itwuzhifei.reggie.pojo.Category;
import com.itwuzhifei.reggie.pojo.Dish;
import com.itwuzhifei.reggie.pojo.Setmeal;
import com.itwuzhifei.reggie.service.CategoryService;
import com.itwuzhifei.reggie.service.DishService;
import com.itwuzhifei.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id 分类id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> queryWrapperDish = new LambdaQueryWrapper<>();
        queryWrapperDish.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(queryWrapperDish);
        if (count1 > 0) {
            throw new CustomerException("当前分类下关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> queryWrapperSetmeal = new LambdaQueryWrapper<>();
        queryWrapperSetmeal.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(queryWrapperSetmeal);
        if (count2 > 0) {
            throw new CustomerException("当前分类下关联了套餐，不能删除");
        }
        //正常删除
        super.removeById(id);
    }
}
