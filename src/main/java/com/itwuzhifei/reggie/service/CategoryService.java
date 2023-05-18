package com.itwuzhifei.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itwuzhifei.reggie.pojo.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
