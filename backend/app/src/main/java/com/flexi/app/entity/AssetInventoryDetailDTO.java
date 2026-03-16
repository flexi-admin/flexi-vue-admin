package com.flexi.app.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class AssetInventoryDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long inventoryId;
    private Long assetId;
    private String assetCode;
    private String assetName;
    private String assetType;
    private String assetLocation;
    private String deptName;
    private String userName;
    private String adminUserName;
    private String status;
    private String labelCode;
    private String sn;
}