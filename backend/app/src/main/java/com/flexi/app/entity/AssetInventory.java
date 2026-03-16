package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("asset_inventory")
public class AssetInventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String inventoryCode;
    private String inventoryName;
    private Long startTime;
    private Long endTime;
    private String status;
    private Long creatorId;
    private Long createTime;
    private Long updateTime;
    private String remark;
}
