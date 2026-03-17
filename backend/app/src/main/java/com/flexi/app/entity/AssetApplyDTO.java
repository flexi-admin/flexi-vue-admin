package com.flexi.app.entity;

import lombok.Data;

@Data
public class AssetApplyDTO {
    private Long id;
    private Long assetId;
    private String assetName;
    private String assetCode;
    private Long userId;
    private String userName;
    private Long deptId;
    private String deptName;
    private Long applyTime;
    private String applyTimeStr;
    private Long approvalTime;
    private String approvalTimeStr;
    private String status;
    private String statusName;
    private String remark;
    private Long createTime;
    private Long updateTime;
}