package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.Asset;
import com.flexi.app.entity.AssetInventoryDetail;
import com.flexi.app.entity.AssetInventoryDetailDTO;
import com.flexi.app.mapper.AssetInventoryDetailMapper;
import com.flexi.app.service.AssetInventoryDetailService;
import com.flexi.app.service.AssetService;
import com.flexi.app.service.AssetTypeService;
import com.flexi.app.service.AssetLocationService;
import io.github.zmxckj.flexiadmin.service.DeptService;
import io.github.zmxckj.flexiadmin.service.UserService;
import io.github.zmxckj.flexiadmin.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetInventoryDetailServiceImpl extends ServiceImpl<AssetInventoryDetailMapper, AssetInventoryDetail> implements AssetInventoryDetailService {

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
    public Page<AssetInventoryDetailDTO> listByInventoryId(Long inventoryId, Page<AssetInventoryDetailDTO> page, String status) {
        QueryWrapper<AssetInventoryDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("inventory_id", inventoryId);
        
        // 添加状态筛选
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        
        // 按ID降序排序
        queryWrapper.orderByDesc("id");
        
        // 查询原始明细数据
        Page<AssetInventoryDetail> detailPage = new Page<>(page.getCurrent(), page.getSize());
        Page<AssetInventoryDetail> resultPage = baseMapper.selectPage(detailPage, queryWrapper);
        
        // 转换为DTO
        List<AssetInventoryDetailDTO> dtoList = resultPage.getRecords().stream().map(detail -> {
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
                    if (asset.getTypeId() != null) {
                        dto.setAssetType(assetTypeService.getById(asset.getTypeId()).getName());
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
        
        // 构建返回的分页对象
        Page<AssetInventoryDetailDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        dtoPage.setRecords(dtoList);
        
        return dtoPage;
    }

    @Override
    public List<Long> getDetailIdsByInventoryId(Long inventoryId) {
        QueryWrapper<AssetInventoryDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("inventory_id", inventoryId);
        queryWrapper.select("id");
        return baseMapper.selectObjs(queryWrapper).stream()
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateBatchDetails(List<AssetInventoryDetail> details) {
        // 为每个明细设置更新时间
        long currentTime = System.currentTimeMillis();
        for (AssetInventoryDetail detail : details) {
            detail.setUpdateTime(currentTime);
        }
        // 批量更新明细数据
        return this.updateBatchById(details);
    }
}

