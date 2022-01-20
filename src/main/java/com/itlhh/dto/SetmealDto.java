package com.itlhh.dto;


import com.itlhh.entity.Setmeal;
import com.itlhh.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
