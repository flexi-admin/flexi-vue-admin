package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("material_detail")
public class MaterialDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long materialId;
    private Long brandId;
    private Long warehouseId;
    private Long locationId;
    private java.math.BigDecimal quantity;
    private String unit;
    private String batchNo;
    private Long expireDate;
    private Integer status;
    private String remark;
    private Long createTime;
    private Long updateTime;
}
