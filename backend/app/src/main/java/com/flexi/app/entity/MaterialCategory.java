package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("material_category")
public class MaterialCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long parentId;
    private String path;
    private Integer level;
    private String remark;
    private Integer status;
    private Long createTime;
    private Long updateTime;
}
