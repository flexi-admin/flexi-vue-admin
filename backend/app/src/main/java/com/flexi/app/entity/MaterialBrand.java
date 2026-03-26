package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("material_brand")
public class MaterialBrand {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String name;
    private String sn;
    private String specification;
    private String unit;
    private String packUnit;
    private Integer packQuantity;
    private String packBarcode;
    private String brand;
    private java.math.BigDecimal price;
    private String remark;
    private Integer status;
    private Long createTime;
    private Long updateTime;
}
