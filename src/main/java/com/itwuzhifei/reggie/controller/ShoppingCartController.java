package com.itwuzhifei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itwuzhifei.reggie.common.BaseContext;
import com.itwuzhifei.reggie.common.R;
import com.itwuzhifei.reggie.pojo.ShoppingCart;
import com.itwuzhifei.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    //查询所有购物车信息
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCarts);
    }

    //添加菜品或者套餐到购物车
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置当前用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());

        Long dishId = shoppingCart.getDishId();
        //添加的是菜品
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }

        Long setmealId = shoppingCart.getSetmealId();
        //添加的是套餐
        if (setmealId != null) {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart sp = shoppingCartService.getOne(queryWrapper);
        //判断购物车是否存在该商品
        if (sp == null) {
            //不存在则设置份数为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            sp = shoppingCart;
        } else {
            //存在则在原来的份数上加1
            sp.setNumber(sp.getNumber() + 1);
            shoppingCartService.updateById(sp);
        }

        return R.success(sp);
    }

    //清空购物车
    @DeleteMapping("/clean")
    public R<String> clean(){
        //清除当前用户id的购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }

    //删除加入购物车的菜品或套餐一份
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("要减少一份的菜品或套餐id：{}", shoppingCart.getId());

        //查询当前用户购物车菜品或套餐数量是否为一份
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        //判断要减少的是菜品还是套餐
        if (shoppingCart.getDishId() != null) {
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);

        //如果为一份，则直接将该条菜品或者套餐删除;如果大于一份，则将数量减一
        if (cart.getNumber() == 1) {
            shoppingCartService.removeById(cart.getId());
            cart = shoppingCart;
        }else {
            cart.setNumber(cart.getNumber() - 1);
            shoppingCartService.updateById(cart);
        }

        return R.success(cart);
    }
}
