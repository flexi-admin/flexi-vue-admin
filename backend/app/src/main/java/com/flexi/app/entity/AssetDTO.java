package com.flexi.app.entity;

import lombok.Data;

@Data
public class AssetDTO {
    private Long id;
    private String name;
    private String code;
    private String typeCode;
    private String customTypeCode; // 自定义类型编码
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
    private String warehouseDate; // 入库日期
    private String enableDate; // 启用日期
    private Double price;
    private String status;
    private String statusName; // 资产状态名称
    private String remark;
    private Integer isDeleted;
    private String createTime;
    private String updateTime;
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
    private Double accumulatedDepreciationValue; // 已计提折旧价值
    private String unit;
    private String unitName; // 计量单位名称
    private Long creatorId;
    private Long updaterId;
}