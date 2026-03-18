package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("asset_apply")
public class AssetApply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long assetId;
    private Long userId;
    private Long deptId;
    private Long applyTime;
    private Long approvalTime;
    private String status;
    private String remark;
    private Long createTime;
    private Long updateTime;
    private Long approverId;
    private String rejectReason;
}