package com.flexi.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("asset_supplier")
public class AssetSupplier {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String contactPerson;
    private String contactPhone;
    private String contactAddress;
    private String remark;
    private Integer isDeleted;
    private Long createTime;
    private Long updateTime;
}
