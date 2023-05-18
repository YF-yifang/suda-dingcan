package com.itwuzhifei.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itwuzhifei.reggie.dto.SetmealDto;
import com.itwuzhifei.reggie.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //新增套餐，同时保存套餐和菜品关联关系
    void saveWithDish(SetmealDto setmealDto);

    //根据id批量删除套餐信息
    void deleteBatchByIds(List<Long> ids);

    //根据id查询套餐信息
    SetmealDto getSetmealById(Long id);

    //根据id修改套餐信息
    void updateWithSetmeal(SetmealDto setmealDto);

    //批量启售停售
    void updateStatus(int status, List<Long> ids);
}
