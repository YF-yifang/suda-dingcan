package com.itwuzhifei.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itwuzhifei.reggie.mapper.OrderDetailMapper;
import com.itwuzhifei.reggie.pojo.OrderDetail;
import com.itwuzhifei.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
