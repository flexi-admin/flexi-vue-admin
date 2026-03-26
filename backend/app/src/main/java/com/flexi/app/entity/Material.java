package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("material")
public class Material {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private Long categoryId;
    private Long brandId;
    private java.math.BigDecimal totalQuantity;
    private Integer status;
    private String remark;
    private Long createTime;
    private Long updateTime;
}
