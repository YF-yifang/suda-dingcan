package com.itwuzhifei.reggie.dto;

import com.itwuzhifei.reggie.pojo.Setmeal;
import com.itwuzhifei.reggie.pojo.SetmealDish;
import lombok.Data;

import java.util.List;


@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
