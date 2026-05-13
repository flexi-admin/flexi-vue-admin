package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("asset_location")
public class AssetLocation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long parentId;
    private String path;
    private Integer level;
    private String remark;
    private Integer status;
    private String beaconMac;
    private String beacon2Mac;
    private String ooomapUrl;
    private String ooomapAppid;
    private Double x;
    private Double y;
    private Double z;
    private Long tenantId;
    private Long createTime;
    private Long updateTime;
}