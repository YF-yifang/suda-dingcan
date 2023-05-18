package com.itwuzhifei.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itwuzhifei.reggie.common.CustomerException;
import com.itwuzhifei.reggie.dto.SetmealDto;
import com.itwuzhifei.reggie.mapper.SetmealMapper;
import com.itwuzhifei.reggie.pojo.Dish;
import com.itwuzhifei.reggie.pojo.Setmeal;
import com.itwuzhifei.reggie.pojo.SetmealDish;
import com.itwuzhifei.reggie.service.DishService;
import com.itwuzhifei.reggie.service.SetmealDishService;
import com.itwuzhifei.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private SetmealDishService setmealDishService;

    @Resource
    private DishService dishService;

    /**
     * 新增套餐，同时保存套餐和菜品关联关系
     *
     * @param setmealDto 套餐信息
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        if (setmealDishes != null) {
            setmealDishes = setmealDishes.stream().map((item) -> {
                item.setSetmealId(setmealDto.getId());
                return item;
            }).collect(Collectors.toList());
        }

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 根据id批量删除套餐信息
     *
     * @param ids 需要删除的id
     */
    @Transactional
    @Override
    public void deleteBatchByIds(List<Long> ids) {
        //判断套餐是否在停售状态
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(setmealLambdaQueryWrapper);
        if (count > 0) {
            throw new CustomerException("套餐正在售卖中，不能删除");
        }

        //删除套餐
        this.removeByIds(ids);

        //删除套餐菜品关联关系
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }

    /**
     * 根据id查询套餐信息
     *
     * @param id 套餐id
     * @return 套餐信息
     */
    @Transactional
    @Override
    public SetmealDto getSetmealById(Long id) {
        //查询套餐基本信息
        Setmeal setmeal = this.getById(id);

        //对象拷贝
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        //查询套餐关联字段信息
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> dishes = setmealDishService.list(setmealDishLambdaQueryWrapper);

        setmealDto.setSetmealDishes(dishes);

        return setmealDto;
    }

    /**
     * 根据id修改套餐信息
     *
     * @param setmealDto 要修改的套餐信息
     */
    @Transactional
    @Override
    public void updateWithSetmeal(SetmealDto setmealDto) {
        //修改套餐基本信息
        this.updateById(setmealDto);

        //清理当前套餐菜品
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

        //重新添加提交的套餐菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 批量启售停售
     *
     * @param status 启售停售标志
     * @param ids    启售停售id
     */
    @Transactional
    @Override
    public void updateStatus(int status, List<Long> ids) {
        //如果要启售，判断改套餐下所有的菜品是否都在启售状态
        if (status == 1) {
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
            List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);

            for (SetmealDish setmealDish : setmealDishes) {
                LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
                dishLambdaQueryWrapper.eq(Dish::getId, setmealDish.getDishId());
                Dish dish = dishService.getOne(dishLambdaQueryWrapper);
                if (dish.getStatus() == 0) {
                    throw new CustomerException("套餐内存在未启售的菜品");
                }
            }

        }
        List<Setmeal> setmealList = ids.stream().map((id) -> {
            Setmeal setmeal = this.getById(id);
            setmeal.setStatus(status);
            return setmeal;
        }).collect(Collectors.toList());
        this.updateBatchById(setmealList);
    }
}
