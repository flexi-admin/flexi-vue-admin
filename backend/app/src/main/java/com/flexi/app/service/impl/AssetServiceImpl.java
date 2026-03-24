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
import com.flexi.app.service.*;
import io.github.zmxckj.flexiadmin.entity.Dict;
import io.github.zmxckj.flexiadmin.security.SecurityUtils;
import io.github.zmxckj.flexiadmin.service.DictService;
import io.github.zmxckj.flexiadmin.service.UserService;
import io.github.zmxckj.flexiadmin.service.DeptService;
import io.github.zmxckj.flexiadmin.utils.DataScopeHelper;
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

    @Override
    public AssetDTO getAssetById(Long id) {
        // 根据id查找资产
        DataScopeHelper.set("user_id = " + SecurityUtils.getCurrentUserId());
        Asset asset = baseMapper.selectById(id);
        DataScopeHelper.clear();
        // 转换为DTO并返回
        return asset != null ? convertToDTO(asset) : null;
    }

    @Override
    public List<java.util.Map<String, String>> getBatchPrintData() {
        // 构建查询条件，查询code = label_code的资产
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.apply("code = label_code");
        
        // 执行查询
        List<Asset> assets = baseMapper.selectList(queryWrapper);
        
        // 转换为前端需要的数据格式
        return assets.stream().map(asset -> {
            java.util.Map<String, String> printData = new java.util.HashMap<>();
            printData.put("CardName", asset.getName());
            printData.put("CardSerial", asset.getCode());
            return printData;
        }).collect(java.util.stream.Collectors.toList());
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

    @Override
    public java.util.Map<String, Object> getAssetStatistics() {
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        
        // 从数据库获取实际数据
        // 1. 资产总数
        long totalAssets = count();
        statistics.put("totalAssets", totalAssets);
        
        // 2. 固定资产净值
        double fixedAssetValue = 0;
        List<Asset> assets = list();
        for (Asset asset : assets) {
            if (asset.getCurrentValue() != null) {
                fixedAssetValue += asset.getCurrentValue();
            }
        }
        statistics.put("fixedAssetValue", fixedAssetValue);
        
        // 3. 本年新增资产
        // 计算本年开始时间戳
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.MONTH, 0);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        long startOfYear = calendar.getTimeInMillis();
        
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> yearQuery = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        yearQuery.ge("create_time", startOfYear);
        long newAssetsThisYear = count(yearQuery);
        statistics.put("newAssetsThisYear", newAssetsThisYear);
        
        // 4. 本年报废资产
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> scrapQuery = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        scrapQuery.eq("status", "scrapped");
        scrapQuery.ge("update_time", startOfYear);
        long scrappedAssetsThisYear = count(scrapQuery);
        statistics.put("scrappedAssetsThisYear", scrappedAssetsThisYear);
        
        // 5. 年折旧费
        // 简单计算：总资产原值 - 总净值
        double totalOriginalValue = 0;
        double totalCurrentValue = 0;
        for (Asset asset : assets) {
            if (asset.getPrice() != null) {
                totalOriginalValue += asset.getPrice();
            }
            if (asset.getCurrentValue() != null) {
                totalCurrentValue += asset.getCurrentValue();
            }
        }
        double annualDepreciation = totalOriginalValue - totalCurrentValue;
        statistics.put("annualDepreciation", annualDepreciation);
        
        return statistics;
    }

    @Override
    public List<java.util.Map<String, Object>> getAssetTypeDistribution() {
        List<java.util.Map<String, Object>> distribution = new java.util.ArrayList<>();
        
        // 从数据库获取实际数据
        // 1. 查询所有资产类型
        List<AssetType> assetTypes = assetTypeService.list();
        
        // 2. 统计每种类型的资产数量
        for (AssetType type : assetTypes) {
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("type_id", type.getId());
            long count = count(queryWrapper);
            
            // 构建分布数据
            java.util.Map<String, Object> typeData = new java.util.HashMap<>();
            typeData.put("name", type.getName());
            typeData.put("value", count);
            distribution.add(typeData);
        }
        
        return distribution;
    }

    @Override
    public List<java.util.Map<String, Object>> getAssetStatusDistribution() {
        List<java.util.Map<String, Object>> distribution = new java.util.ArrayList<>();
        
        // 从数据库获取实际数据
        // 1. 获取所有资产状态字典
        List<Dict> statusDicts = dictService.listByType("asset_status");
        
        // 2. 统计每种状态的资产数量
        for (Dict dict : statusDicts) {
            String statusCode = dict.getCode();
            String statusName = dict.getValue();
            
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("status", statusCode);
            long count = count(queryWrapper);
            
            // 构建分布数据
            java.util.Map<String, Object> statusData = new java.util.HashMap<>();
            statusData.put("name", statusName);
            statusData.put("value", count);
            distribution.add(statusData);
        }
        
        return distribution;
    }

    @Override
    public List<java.util.Map<String, Object>> getAssetStatusDistributionByType(String type) {
        List<java.util.Map<String, Object>> distribution = new java.util.ArrayList<>();
        
        // 从数据库获取实际数据
        // 1. 获取所有资产状态字典
        List<Dict> statusDicts = dictService.listByType("asset_status");
        
        // 2. 根据类型获取资产类型ID
        Long typeId = null;
        if (!"all".equals(type)) {
            // 这里需要根据类型名称获取类型ID，假设类型名称与前端传递的type参数对应
            // 实际项目中可能需要调整逻辑
            List<AssetType> assetTypes = assetTypeService.list();
            for (AssetType assetType : assetTypes) {
                if (assetType.getName().contains(type)) {
                    typeId = assetType.getId();
                    break;
                }
            }
        }
        
        // 3. 统计每种状态的资产数量
        for (Dict dict : statusDicts) {
            String statusCode = dict.getCode();
            String statusName = dict.getValue();
            
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("status", statusCode);
            if (typeId != null) {
                queryWrapper.eq("type_id", typeId);
            }
            long count = count(queryWrapper);
            
            // 构建分布数据
            java.util.Map<String, Object> statusData = new java.util.HashMap<>();
            statusData.put("name", statusName);
            statusData.put("value", count);
            distribution.add(statusData);
        }
        
        return distribution;
    }

    @Override
    public List<java.util.Map<String, Object>> getIdleRateAnalysis() {
        List<java.util.Map<String, Object>> analysis = new java.util.ArrayList<>();
        
        // 从数据库获取实际数据
        // 1. 查询所有资产类型
        List<AssetType> assetTypes = assetTypeService.list();
        
        // 2. 计算每种类型的闲置率
        for (AssetType type : assetTypes) {
            // 统计该类型的资产总数
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> totalQuery = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            totalQuery.eq("type_id", type.getId());
            long totalCount = count(totalQuery);
            
            // 统计该类型的闲置资产数量
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> idleQuery = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            idleQuery.eq("type_id", type.getId());
            idleQuery.eq("status", "idle"); // 假设闲置状态的code是"idle"
            long idleCount = count(idleQuery);
            
            // 计算闲置率
            String idleRate = "0%";
            if (totalCount > 0) {
                int rate = (int) (idleCount * 100 / totalCount);
                idleRate = rate + "%";
            }
            
            // 构建分析数据
            java.util.Map<String, Object> typeData = new java.util.HashMap<>();
            typeData.put("category", type.getName());
            typeData.put("rate", idleRate);
            analysis.add(typeData);
        }
        
        return analysis;
    }

    @Override
    public List<java.util.Map<String, Object>> getAmountStatistics() {
        List<java.util.Map<String, Object>> statistics = new java.util.ArrayList<>();
        
        // 从数据库获取实际数据
        // 1. 计算过去6个月的月份
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int currentMonth = calendar.get(java.util.Calendar.MONTH);
        
        for (int i = 5; i >= 0; i--) {
            // 基于当前月份计算过去的月份
            int targetMonth = (currentMonth - i + 12) % 12;
            int month = targetMonth + 1;
            String monthStr = month + "月";
            
            // 计算该月的开始和结束时间戳
            java.util.Calendar monthStart = java.util.Calendar.getInstance();
            // 计算年份：如果目标月份大于当前月份，说明是上一年
            int currentYear = monthStart.get(java.util.Calendar.YEAR);
            int targetYear = currentYear;
            if (targetMonth > currentMonth) {
                targetYear = currentYear - 1;
            }
            monthStart.set(java.util.Calendar.YEAR, targetYear);
            monthStart.set(java.util.Calendar.MONTH, targetMonth);
            monthStart.set(java.util.Calendar.DAY_OF_MONTH, 1);
            monthStart.set(java.util.Calendar.HOUR_OF_DAY, 0);
            monthStart.set(java.util.Calendar.MINUTE, 0);
            monthStart.set(java.util.Calendar.SECOND, 0);
            long startTimestamp = monthStart.getTimeInMillis();
            
            java.util.Calendar monthEnd = java.util.Calendar.getInstance();
            monthEnd.set(java.util.Calendar.YEAR, targetYear);
            monthEnd.set(java.util.Calendar.MONTH, targetMonth + 1);
            monthEnd.set(java.util.Calendar.DAY_OF_MONTH, 0);
            monthEnd.set(java.util.Calendar.HOUR_OF_DAY, 23);
            monthEnd.set(java.util.Calendar.MINUTE, 59);
            monthEnd.set(java.util.Calendar.SECOND, 59);
            long endTimestamp = monthEnd.getTimeInMillis();
            
            // 统计该月的采购金额
            double purchaseValue = 0;
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> purchaseQuery = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            purchaseQuery.between("create_time", startTimestamp, endTimestamp);
            List<Asset> purchaseAssets = list(purchaseQuery);
            for (Asset asset : purchaseAssets) {
                if (asset.getPrice() != null) {
                    purchaseValue += asset.getPrice();
                }
            }
            
            // 统计到该月为止的资产总值
            double totalValue = 0;
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Asset> totalQuery = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            totalQuery.le("create_time", endTimestamp);
            List<Asset> totalAssets = list(totalQuery);
            for (Asset asset : totalAssets) {
                if (asset.getCurrentValue() != null) {
                    totalValue += asset.getCurrentValue();
                } else if (asset.getPrice() != null) {
                    totalValue += asset.getPrice();
                }
            }
            
            // 构建月份数据
            java.util.Map<String, Object> monthData = new java.util.HashMap<>();
            monthData.put("month", monthStr);
            monthData.put("purchaseValue", purchaseValue);
            monthData.put("totalValue", totalValue);
            statistics.add(monthData);
        }
        
        return statistics;
    }




}