package com.flexi.app.entity;

import lombok.Data;

@Data
public class AssetDTO {
    private Long id;
    private String name;
    private String code;
    private Long typeId;
    private String typeName; // 资产类型名称
    private Long locationId;
    private String locationName; // 资产位置名称
    private String specification;
    private String model;
    private String manufacturer;
    private Long supplierId;
    private String supplierName; // 供应商名称
    private String purchaseDate;
    private Double price;
    private String status;
    private String statusName; // 资产状态名称
    private String remark;
    private Integer isDeleted;
    private Long createTime;
    private Long updateTime;
    private String image;
    private String labelType;
    private String labelCode;
    private Long adminUserId;
    private String adminUserName; // 管理员名称
    private Long userId;
    private String userName; // 使用人名称
    private Long deptId;
    private String deptName; // 部门名称
    private String sn;
    private String source;
    private String sourceName; // 资产来源名称
    private Double currentValue;
    private String unit;
    private String unitName; // 计量单位名称
    private Long creatorId;
    private Long updaterId;
}