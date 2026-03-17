package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.Asset;
import com.flexi.app.entity.AssetDTO;
import com.flexi.app.entity.AssetLocation;
import com.flexi.app.entity.AssetSupplier;
import com.flexi.app.entity.AssetType;
import com.flexi.app.mapper.AssetMapper;
import com.flexi.app.service.AssetService;
import com.flexi.app.service.AssetTypeService;
import com.flexi.app.service.AssetLocationService;
import com.flexi.app.service.AssetSupplierService;
import io.github.zmxckj.flexiadmin.service.DictService;
import io.github.zmxckj.flexiadmin.service.UserService;
import io.github.zmxckj.flexiadmin.service.DeptService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {

    @Autowired
    private AssetTypeService assetTypeService;

    @Autowired
    private AssetLocationService assetLocationService;

    @Autowired
    private AssetSupplierService assetSupplierService;

    @Autowired
    private DictService dictService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    @Override
    public List<AssetDTO> listWithDetails() {
        // 查询所有资产
        List<Asset> assets = list();
        
        // 转换为DTO
        return assets.stream().map(asset -> {
            return convertToDTO(asset);
        }).collect(Collectors.toList());
    }

    @Override
    public IPage<AssetDTO> listWithDetails(Integer page, Integer size) {
        // 创建分页对象
        Page<Asset> pageInfo = new Page<>(page, size);
        
        // 执行分页查询
        IPage<Asset> assetPage = baseMapper.selectPage(pageInfo, null);
        
        // 转换为DTO并返回
        return assetPage.convert(this::convertToDTO);
    }

    @Override
    public IPage<AssetDTO> listMyAssets(Integer page, Integer size, Long userId) {
        // 创建分页对象
        Page<Asset> pageInfo = new Page<>(page, size);
        
        // 构建查询条件，根据userId查询
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        // 执行分页查询
        IPage<Asset> assetPage = baseMapper.selectPage(pageInfo, queryWrapper);
        
        // 转换为DTO并返回
        return assetPage.convert(this::convertToDTO);
    }

    @Override
    public IPage<AssetDTO> listWithDetails(Integer page, Integer size, String name, String status, Long typeId) {
        // 创建分页对象
        Page<Asset> pageInfo = new Page<>(page, size);
        
        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        // 添加过滤条件
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        if (typeId != null) {
            queryWrapper.eq("type_id", typeId);
        }
        
        // 执行分页查询
        IPage<Asset> assetPage = baseMapper.selectPage(pageInfo, queryWrapper);
        
        // 转换为DTO并返回
        return assetPage.convert(this::convertToDTO);
    }

    @Override
    public List<Asset> listAssetsWithoutLabelCode() {
        // 构建查询条件，查询code = label_code的资产
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.apply("code = label_code");
        
        // 执行查询
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void updateLabelCode(java.util.Map<String, String> codeLabelMap) {
        // 遍历codeLabelMap，更新每个资产的label_code
        for (java.util.Map.Entry<String, String> entry : codeLabelMap.entrySet()) {
            String code = entry.getKey();
            String labelCode = entry.getValue();
            
            // 构建查询条件，根据code查找资产
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("code", code);
            
            // 查找资产
            Asset asset = baseMapper.selectOne(queryWrapper);
            if (asset != null) {
                // 更新label_code
                asset.setLabelCode(labelCode);
                asset.setUpdateTime(System.currentTimeMillis());
                // 保存更新
                baseMapper.updateById(asset);
            }
        }
    }

    /**
     * 将Asset转换为AssetDTO
     */
    private AssetDTO convertToDTO(Asset asset) {
        AssetDTO dto = new AssetDTO();
        BeanUtils.copyProperties(asset, dto);
        
        // 填充资产类型名称
        if (asset.getTypeId() != null) {
            AssetType type = assetTypeService.getById(asset.getTypeId());
            if (type != null) {
                dto.setTypeName(type.getName());
            }
        }
        
        // 填充资产位置名称
        if (asset.getLocationId() != null) {
            AssetLocation location = assetLocationService.getById(asset.getLocationId());
            if (location != null) {
                dto.setLocationName(location.getName());
            }
        }
        
        // 填充供应商名称
        if (asset.getSupplierId() != null) {
            AssetSupplier supplier = assetSupplierService.getById(asset.getSupplierId());
            if (supplier != null) {
                dto.setSupplierName(supplier.getName());
            }
        }
        
        // 填充资产状态名称
        if (asset.getStatus() != null) {
            String statusName = dictService.getDictLabel("asset_status", asset.getStatus());
            dto.setStatusName(statusName);
        }
        
        // 填充管理员名称
        if (asset.getAdminUserId() != null) {
            String adminUserName = userService.getUserNameById(asset.getAdminUserId());
            dto.setAdminUserName(adminUserName);
        }
        
        // 填充使用人名称
        if (asset.getUserId() != null) {
            String userName = userService.getUserNameById(asset.getUserId());
            dto.setUserName(userName);
        }
        
        // 填充部门名称
        if (asset.getDeptId() != null) {
            String deptName = deptService.getDeptNameById(asset.getDeptId());
            dto.setDeptName(deptName);
        }
        
        // 填充资产来源名称
        if (asset.getSource() != null) {
            String sourceName = dictService.getDictLabel("asset_source", asset.getSource());
            dto.setSourceName(sourceName);
        }
        
        // 填充计量单位名称
        if (asset.getUnit() != null) {
            String unitName = dictService.getDictLabel("asset_unit", asset.getUnit());
            dto.setUnitName(unitName);
        }
        
        return dto;
    }
}