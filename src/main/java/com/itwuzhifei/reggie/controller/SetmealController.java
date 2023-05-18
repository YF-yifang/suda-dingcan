package com.itwuzhifei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itwuzhifei.reggie.common.R;
import com.itwuzhifei.reggie.dto.SetmealDto;
import com.itwuzhifei.reggie.pojo.Category;
import com.itwuzhifei.reggie.pojo.Setmeal;
import com.itwuzhifei.reggie.service.CategoryService;
import com.itwuzhifei.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    @Resource
    private CategoryService categoryService;

    //新增套餐
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(String name, int page, int pageSize) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        //对象拷贝
        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");

        List<SetmealDto> records = pageInfo.getRecords().stream().map((item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        })).collect(Collectors.toList());

        setmealDtoPage.setRecords(records);

        return R.success(setmealDtoPage);
    }

    //批量删除
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.deleteBatchByIds(ids);
        return R.success("套餐信息删除成功");
    }

    //批量启售停售
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") int status, @RequestParam List<Long> ids) {
        log.info("status：{}, ids：{}", status, ids.toArray());
        setmealService.updateStatus(status, ids);
        return R.success("套餐信息修改成功");
    }

    //根据id查询套餐信息
    @GetMapping("{id}")
    public R<SetmealDto> getSetmealById(@PathVariable("id") Long id) {
        SetmealDto setmealDto = setmealService.getSetmealById(id);
        return R.success(setmealDto);
    }

    //修改套餐信息
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithSetmeal(setmealDto);
        return R.success("套餐信息修改成功");
    }

    //查询所有套餐信息
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        return R.success(setmeals);
    }
}
