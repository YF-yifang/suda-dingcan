package com.itwuzhifei.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itwuzhifei.reggie.mapper.AddressBookMapper;
import com.itwuzhifei.reggie.pojo.AddressBook;
import com.itwuzhifei.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
