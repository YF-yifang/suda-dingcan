package com.itwuzhifei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itwuzhifei.reggie.common.R;
import com.itwuzhifei.reggie.dto.DishDto;
import com.itwuzhifei.reggie.pojo.Category;
import com.itwuzhifei.reggie.pojo.Dish;
import com.itwuzhifei.reggie.pojo.DishFlavor;
import com.itwuzhifei.reggie.service.CategoryService;
import com.itwuzhifei.reggie.service.DishFlavorService;
import com.itwuzhifei.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishFlavorService dishFlavorService;

    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page<DishDto>> page(String name, int page, int pageSize) {
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //查询结果
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    //根据id查询菜品和口味信息
    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable("id") Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    //修改菜品
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品信息成功");
    }

    //菜品批量启售停售
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") int status, Long[] ids) {
        log.info("status：{}, ids：{}", status, Arrays.stream(ids).toArray());

        List<Dish> dishes = Arrays.stream(ids).map((id) -> {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishes);
        return R.success("菜品信息修改成功");
    }

    //批量删除
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("删除的id为为：{}", ids.toArray());
        dishService.deleteBatchByIds(ids);
        return R.success("删除成功");
    }

    /*//根据条件查询对应菜品数据
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        if (dish.getCategoryId() != null) {
            queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        } else {
            queryWrapper.like(Dish::getName, dish.getName());
        }
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishService.list(queryWrapper);
        return R.success(dishes);
    }*/

    //根据条件查询对应菜品数据
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        if (dish.getCategoryId() != null) {
            queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        } else {
            queryWrapper.like(Dish::getName, dish.getName());
        }
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = dishes.stream().map((item) -> {
            //查询菜品下对应的口味信息
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //判断菜品是否设置了口味信息
            if (dishFlavors != null) {
                dishDto.setFlavors(dishFlavors);
            }
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}
