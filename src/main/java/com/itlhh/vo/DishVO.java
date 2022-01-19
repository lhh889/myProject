package com.itlhh.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author lhh
 * @Date 2022/1/19 11:06
 */
@Data
public class DishVO {
    private Long id;
    private String name;
    private String categoryName;
    private String image;
    private BigDecimal price;
    private int status;
    private LocalDateTime updateTime;
}
