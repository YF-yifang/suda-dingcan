package com.itwuzhifei.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itwuzhifei.reggie.common.CustomerException;
import com.itwuzhifei.reggie.dto.DishDto;
import com.itwuzhifei.reggie.mapper.DishMapper;
import com.itwuzhifei.reggie.pojo.Dish;
import com.itwuzhifei.reggie.pojo.DishFlavor;
import com.itwuzhifei.reggie.pojo.SetmealDish;
import com.itwuzhifei.reggie.service.DishFlavorService;
import com.itwuzhifei.reggie.service.DishService;
import com.itwuzhifei.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private SetmealDishService setmealDishService;

    /**
     * 新增菜品，同时保存口味信息
     *
     * @param dishDto 菜品和口味信息
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        /*for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }*/
        if (flavors != null) {
            flavors = flavors.stream().map((item) -> {
                item.setDishId(dishId);
                return item;
            }).collect(Collectors.toList());
        }
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * //根据id查询菜品信息和对应的口味信息
     *
     * @param id 菜品id
     * @return 菜品和口味信息
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);

        //对象拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 修改菜品信息和对应的口味信息
     *
     * @param dishDto 菜品和口味信息
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        //清理当前菜品口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //重新添加提交过来的菜品口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        if (flavors != null) {
            flavors = flavors.stream().map((item) -> {
                item.setDishId(dishDto.getId());
                return item;
            }).collect(Collectors.toList());
        }
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id批量删除菜品信息和对应的口味信息
     * @param ids 要删除的id
     */
    @Transactional
    @Override
    public void deleteBatchByIds(List<Long> ids) {
        //查询菜品是否停售
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId, ids);
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
        int dishCount = this.count(dishLambdaQueryWrapper);
        //菜品未停售，无法删除
        if (dishCount > 0) {
            throw new CustomerException("菜品正在售卖中，不能删除");
        }
        //查询菜品是否存在在套餐中
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.in(SetmealDish::getDishId, ids);
        int setmealDishCount = setmealDishService.count(setmealDishQueryWrapper);
        //菜品存在套餐中，无法删除
        if (setmealDishCount > 0) {
            throw new CustomerException("菜品存在套餐中，无法删除");
        }

        //删除菜品口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(queryWrapper);

        //删除菜品信息
        this.removeByIds(ids);
    }
}
