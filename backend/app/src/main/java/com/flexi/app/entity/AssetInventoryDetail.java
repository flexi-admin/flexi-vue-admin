package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("asset_inventory_detail")
public class AssetInventoryDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long inventoryId;
    private Long assetId;
    private Integer expectedQuantity;
    private Integer actualQuantity;
    private String status;
    private String remark;
    private Long createTime;
    private Long updateTime;
}
