package com.itwuzhifei.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itwuzhifei.reggie.pojo.Orders;

public interface OrderService extends IService<Orders> {

    //提交订单
    void submit(Orders orders);
}
