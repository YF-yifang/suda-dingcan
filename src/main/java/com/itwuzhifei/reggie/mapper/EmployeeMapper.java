package com.itwuzhifei.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itwuzhifei.reggie.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
