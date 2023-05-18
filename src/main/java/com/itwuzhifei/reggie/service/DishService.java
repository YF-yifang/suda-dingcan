package com.itwuzhifei.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itwuzhifei.reggie.dto.DishDto;
import com.itwuzhifei.reggie.pojo.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，同时保存菜品对应的口味信息
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    //根据id修改菜品和对应的口味信息
    void updateWithFlavor(DishDto dishDto);

    //根据id批量删除菜品信息和对应的口味信息
    void deleteBatchByIds(List<Long> ids);
}
