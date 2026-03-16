package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("asset_inventory")
public class AssetInventory implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String inventoryCode;
    private String inventoryName;
    private String inventoryType;
    private String inventoryDepts;
    private String inventoryCategories;
    private Long startTime;
    private Long endTime;
    private String status;
    private Long creatorId;
    private Long createTime;
    private Long updateTime;
    private String remark;
}
