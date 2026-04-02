package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("asset")
public class Asset {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private String typeCode;
    private Long locationId;
    private String specification;
    private String model;
    private String manufacturer;
    private Long supplierId;
    private String purchaseDate;
    private Double price;
    private String status;
    private String remark;
    private Integer isDeleted;
    private Long createTime;
    private Long updateTime;
    private String image;
    private String labelType;
    private String labelCode;
    private Long adminUserId;
    private Long userId;
    private Long deptId;
    private String sn;
    private String source;
    private Double currentValue;
    private String unit;
    private Long creatorId;
    private Long updaterId;
}