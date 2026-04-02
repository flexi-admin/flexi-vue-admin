package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.*;
import com.flexi.app.mapper.AssetInventoryMapper;
import com.flexi.app.service.*;
import io.github.zmxckj.flexiadmin.entity.User;
import io.github.zmxckj.flexiadmin.service.DeptService;
import io.github.zmxckj.flexiadmin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssetInventoryServiceImpl extends ServiceImpl<AssetInventoryMapper, AssetInventory> implements AssetInventoryService {

    @Autowired
    private AssetInventoryDetailService assetInventoryDetailService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetTypeService assetTypeService;

    @Autowired
    private AssetLocationService assetLocationService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private UserService userService;

    @Override
    public void syncInventory(Long inventoryId, String inventoryType, String inventoryDepts, String inventoryCategories) {
        // 1. 获取现有盘点明细
        QueryWrapper<AssetInventoryDetail> detailQuery = new QueryWrapper<>();
        detailQuery.eq("inventory_id", inventoryId);
        List<AssetInventoryDetail> existingDetails = assetInventoryDetailService.list(detailQuery);
        Set<Long> existingAssetIds = existingDetails.stream()
                .map(AssetInventoryDetail::getAssetId)
                .collect(Collectors.toSet());

        // 2. 根据盘点类型获取需要同步的资产
        List<Asset> assetsToSync = new ArrayList<>();
        if ("all".equals(inventoryType)) {
            // 全部盘点，获取所有资产
            assetsToSync = assetService.list();
        } else if ("dept".equals(inventoryType) && !inventoryDepts.isEmpty()) {
            // 按部门盘点，获取指定部门的资产
            List<Long> deptIds = Arrays.stream(inventoryDepts.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            QueryWrapper<Asset> assetQuery = new QueryWrapper<>();
            assetQuery.in("dept_id", deptIds);
            assetsToSync = assetService.list(assetQuery);
        } else if ("category".equals(inventoryType) && !inventoryCategories.isEmpty()) {
            // 按分类盘点，获取指定分类的资产
            List<String> categoryCodes = Arrays.stream(inventoryCategories.split(","))
                    .collect(Collectors.toList());
            QueryWrapper<Asset> assetQuery = new QueryWrapper<>();
            assetQuery.in("type_code", categoryCodes);
            assetsToSync = assetService.list(assetQuery);
        }

        // 3. 处理增量数据
        Set<Long> assetIdsToSync = assetsToSync.stream()
                .map(Asset::getId)
                .collect(Collectors.toSet());

        // 新增的数据
        List<AssetInventoryDetail> newDetails = new ArrayList<>();
        for (Asset asset : assetsToSync) {
            if (!existingAssetIds.contains(asset.getId())) {
                AssetInventoryDetail detail = new AssetInventoryDetail();
                detail.setInventoryId(inventoryId);
                detail.setAssetId(asset.getId());
                detail.setStatus("pending"); // 初始状态为待盘点
                detail.setCreateTime(System.currentTimeMillis());
                detail.setUpdateTime(System.currentTimeMillis());
                newDetails.add(detail);
            }
        }

        // 删除的数据
        List<Long> assetIdsToDelete = existingAssetIds.stream()
                .filter(id -> !assetIdsToSync.contains(id))
                .collect(Collectors.toList());
        if (!assetIdsToDelete.isEmpty()) {
            QueryWrapper<AssetInventoryDetail> deleteQuery = new QueryWrapper<>();
            deleteQuery.eq("inventory_id", inventoryId);
            deleteQuery.in("asset_id", assetIdsToDelete);
            assetInventoryDetailService.remove(deleteQuery);
        }

        // 批量新增
        if (!newDetails.isEmpty()) {
            assetInventoryDetailService.saveBatch(newDetails);
        }
    }

    @Override
    public Map<String, Object> issueInventory(Long inventoryId) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 获取盘点本身数据
        AssetInventory inventory = getById(inventoryId);
        if (inventory == null) {
            return result;
        }
        
        // 2. 获取盘点明细数据
        // 直接获取所有明细，不需要分页
        QueryWrapper<AssetInventoryDetail> detailQuery = new QueryWrapper<>();
        detailQuery.eq("inventory_id", inventoryId);
        List<AssetInventoryDetail> detailList = assetInventoryDetailService.list(detailQuery);
        
        // 转换为DTO
        List<AssetInventoryDetailDTO> details = detailList.stream().map(detail -> {
            AssetInventoryDetailDTO dto = new AssetInventoryDetailDTO();
            dto.setId(detail.getId());
            dto.setInventoryId(detail.getInventoryId());
            dto.setAssetId(detail.getAssetId());
            dto.setStatus(detail.getStatus());
            
            // 从资产对象中获取详细信息
            Asset asset = assetService.getById(detail.getAssetId());
            if (asset != null) {
                    dto.setAssetCode(asset.getCode());
                    dto.setAssetName(asset.getName());
                    dto.setLabelCode(asset.getLabelCode());
                    dto.setSn(asset.getSn());
                    
                    // 获取资产类型名称
                    if (asset.getTypeCode() != null) {
                        AssetType type = assetTypeService.getOne(new QueryWrapper<AssetType>().eq("code", asset.getTypeCode()));
                        if (type != null) {
                            dto.setAssetType(type.getName());
                        }
                    }
                    
                    // 获取资产位置名称
                    if (asset.getLocationId() != null) {
                        dto.setAssetLocation(assetLocationService.getById(asset.getLocationId()).getName());
                    }
                    
                    // 获取部门名称
                    if (asset.getDeptId() != null) {
                        dto.setDeptName(deptService.getDeptNameById(asset.getDeptId()));
                    }
                    
                    // 获取使用人名称
                    if (asset.getUserId() != null) {
                        String userName = userService.getUserNameById(asset.getUserId());
                        dto.setUserName(userName);
                    }
                    
                    // 获取管理员名称
                    if (asset.getAdminUserId() != null) {
                        String adminUserName = userService.getUserNameById(asset.getAdminUserId());
                        dto.setAdminUserName(adminUserName);
                    }
                }
            
            return dto;
        }).collect(Collectors.toList());
        
        // 3. 获取所有资产位置数据
        List<AssetLocationTree> locations = assetLocationService.tree();
        
        // 4. 构建返回数据
        result.put("id", inventory.getId());
        result.put("inventoryCode", inventory.getInventoryCode());
        result.put("inventoryName", inventory.getInventoryName());
        result.put("inventoryType", inventory.getInventoryType());
        result.put("startTime", inventory.getStartTime());
        result.put("endTime", inventory.getEndTime());
        result.put("status", inventory.getStatus());
        result.put("details", details);
        result.put("locations", locations);
        
        return result;
    }
}
