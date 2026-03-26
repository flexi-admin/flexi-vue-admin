package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("material_location")
public class MaterialLocation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long warehouseId;
    private String name;
    private String code;
    private String remark;
    private Integer status;
    private Long createTime;
    private Long updateTime;
}
