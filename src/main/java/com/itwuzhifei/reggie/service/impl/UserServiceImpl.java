package com.itwuzhifei.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itwuzhifei.reggie.mapper.UserMapper;
import com.itwuzhifei.reggie.pojo.User;
import com.itwuzhifei.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
