package com.flexi.app.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flexi.app.entity.Asset;
import com.flexi.app.entity.AssetDTO;
import com.flexi.app.entity.AssetLocation;
import com.flexi.app.service.AssetLocationService;
import com.flexi.app.service.AssetService;
import com.flexi.app.utils.SnowflakeIdGenerator;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.entity.Dept;
import io.github.zmxckj.flexiadmin.excel.service.ExcelImportService;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import io.github.zmxckj.flexiadmin.security.SecurityUtils;
import io.github.zmxckj.flexiadmin.service.DeptService;
import io.github.zmxckj.flexiadmin.utils.DataScopeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/asset")
public class AssetController {

    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetLocationService assetLocationService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private ExcelImportService excelImportService;

    @RequirePermission("asset:list")
    @GetMapping
    public R<com.baomidou.mybatisplus.core.metadata.IPage<AssetDTO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String typeCode) {
        return R.success(assetService.listWithDetails(page, size, name, status, typeCode));
    }

    @RequirePermission("asset:my:list")
    @GetMapping("/my")
    public R<com.baomidou.mybatisplus.core.metadata.IPage<AssetDTO>> listMyAssets(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.success(assetService.listMyAssets(page, size, userId));
    }

    @RequirePermission("asset:add")
    @PostMapping
    public boolean add(@RequestBody Asset asset) {
        // 生成资产编码（使用雪花算法生成全局唯一ID）
        String uuid = cn.hutool.core.util.IdUtil.objectId();
        asset.setCode(uuid);
        // 用资产编码的值赋值给标签编码
        asset.setLabelCode(uuid);
        asset.setCreateTime(LocalDateTime.now());
        asset.setUpdateTime(LocalDateTime.now());
        asset.setIsDeleted(0);
        return assetService.save(asset);
    }

    @RequirePermission("asset:edit")
    @PutMapping
    public R<?> edit(@RequestBody Asset asset) {
        asset.setUpdateTime(LocalDateTime.now());
        asset.setUpdaterId(SecurityUtils.getCurrentUserId());
        assetService.updateById(asset);
        return R.success();
    }

    @RequirePermission("asset:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        assetService.removeById(id);
        return R.success();
    }

    @RequirePermission("asset:list")
    @GetMapping("/without-label-code")
    public R<List<String>> listAssetsWithoutLabelCode() {
        List<Asset> assets = assetService.listAssetsWithoutLabelCode();
        List<String> codes = assets.stream().map(Asset::getCode).collect(java.util.stream.Collectors.toList());
        return R.success(codes);
    }

    @RequirePermission("asset:edit")
    @PostMapping("/update-label-code")
    public R<?> updateLabelCode(@RequestBody java.util.Map<String, String> codeLabelMap) {
        assetService.updateLabelCode(codeLabelMap);
        return R.success();
    }

    @RequirePermission("asset:query")
    @GetMapping("/{id}")
    public R<AssetDTO> getAssetById(@PathVariable Long id) {
        return R.success(assetService.getAssetById(id));
    }

    @RequirePermission("asset:print")
    @GetMapping("/batch-print-data")
    public R<List<java.util.Map<String, String>>> getBatchPrintData() {
        return R.success(assetService.getBatchPrintData());
    }

    @RequirePermission("asset:statistics")
    @GetMapping("/statistics")
    public R<java.util.Map<String, Object>> getAssetStatistics() {
        return R.success(assetService.getAssetStatistics());
    }

    @RequirePermission("asset:statistics")
    @GetMapping("/type-distribution")
    public R<List<java.util.Map<String, Object>>> getAssetTypeDistribution() {
        return R.success(assetService.getAssetTypeDistribution());
    }

    @RequirePermission("asset:statistics")
    @GetMapping("/status-distribution")
    public R<List<java.util.Map<String, Object>>> getAssetStatusDistribution() {
        return R.success(assetService.getAssetStatusDistribution());
    }

    @RequirePermission("asset:statistics")
    @GetMapping("/status-distribution-by-type")
    public R<List<java.util.Map<String, Object>>> getAssetStatusDistributionByType(@RequestParam String type) {
        return R.success(assetService.getAssetStatusDistributionByType(type));
    }

    @RequirePermission("asset:statistics")
    @GetMapping("/idle-rate-analysis")
    public R<List<java.util.Map<String, Object>>> getIdleRateAnalysis() {
        return R.success(assetService.getIdleRateAnalysis());
    }

    @RequirePermission("asset:statistics")
    @GetMapping("/amount-statistics")
    public R<List<java.util.Map<String, Object>>> getAmountStatistics() {
        return R.success(assetService.getAmountStatistics());
    }

    @RequirePermission("asset:import")
    @PostMapping("/import")
    public R<?> importAssets(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            List<Asset> assets = excelImportService.importData(inputStream, Asset.class,2);
            
            List<AssetLocation> existingLocations = assetLocationService.list();
            List<String> existingLocationNames = existingLocations.stream()
                    .map(AssetLocation::getName)
                    .collect(Collectors.toList());
            
            List<Dept> existingDepts = deptService.list();
            List<String> existingDeptNames = existingDepts.stream()
                    .map(Dept::getName)
                    .collect(Collectors.toList());
            
            List<AssetLocation> locationsToSave = new java.util.ArrayList<>();
            List<Dept> deptsToSave = new java.util.ArrayList<>();
            
            for (Asset asset : assets) {
                if (asset.getLocationName() != null && !asset.getLocationName().isEmpty() 
                        && !existingLocationNames.contains(asset.getLocationName())) {
                    AssetLocation newLocation = new AssetLocation();
                    newLocation.setName(asset.getLocationName());
                    newLocation.setParentId(0L);
                    newLocation.setLevel(1);
                    newLocation.setStatus(1);
                    newLocation.setTenantId(SecurityUtils.getCurrentTenantId());
                    newLocation.setCreateTime(System.currentTimeMillis());
                    newLocation.setUpdateTime(System.currentTimeMillis());
                    locationsToSave.add(newLocation);
                    existingLocationNames.add(asset.getLocationName());
                }
                
                if (asset.getDeptName() != null && !asset.getDeptName().isEmpty() 
                        && !existingDeptNames.contains(asset.getDeptName())) {
                    Dept newDept = new Dept();
                    newDept.setName(asset.getDeptName());
                    newDept.setParentId(0L);
                    newDept.setStatus(true);
                    newDept.setCreateTime(LocalDateTime.now());
                    newDept.setUpdateTime(LocalDateTime.now());
                    newDept.setTenantId(SecurityUtils.getCurrentTenantId());
                    deptsToSave.add(newDept);
                    existingDeptNames.add(asset.getDeptName());
                }
            }
            
            if (!locationsToSave.isEmpty()) {
                assetLocationService.saveBatch(locationsToSave);
                existingLocations.addAll(locationsToSave);
                logger.info("批量新增 {} 条资产位置", locationsToSave.size());
            }
            
            if (!deptsToSave.isEmpty()) {
                deptService.saveBatch(deptsToSave);
                existingDepts.addAll(deptsToSave);
                logger.info("批量新增 {} 条部门", deptsToSave.size());
            }
            
            for (Asset asset : assets) {
                if(StringUtils.hasText(asset.getCustomTypeCode())){
                    asset.setTypeCode(asset.getCustomTypeCode().substring(0, 7));
                }
                if (asset.getStatus() == null || asset.getStatus().isEmpty()) {
                    asset.setStatus("in_use");
                }
                if (asset.getLabelType() == null || asset.getLabelType().isEmpty()) {
                    asset.setLabelType("RFID标签");
                }
                if (asset.getSource() == null || asset.getSource().isEmpty()) {
                    asset.setSource("purchased");
                }
                asset.setIsDeleted(0);
                asset.setCreateTime(LocalDateTime.now());
                asset.setUpdateTime(LocalDateTime.now());
                asset.setCreatorId(SecurityUtils.getCurrentUserId());
                asset.setUpdaterId(SecurityUtils.getCurrentUserId());
                
                if (asset.getLocationName() != null && !asset.getLocationName().isEmpty()) {
                    AssetLocation location = existingLocations.stream()
                            .filter(loc -> loc.getName().equals(asset.getLocationName()))
                            .findFirst()
                            .orElse(null);
                    if (location != null) {
                        asset.setLocationId(location.getId());
                    }
                }
                
                if (asset.getDeptName() != null && !asset.getDeptName().isEmpty()) {
                    Dept dept = existingDepts.stream()
                            .filter(d -> d.getName().equals(asset.getDeptName()))
                            .findFirst()
                            .orElse(null);
                    if (dept != null) {
                        asset.setDeptId(dept.getId());
                    }
                }
                
                logger.info("解析到资产实体: {}", asset);
            }
            
            logger.info("共解析到 {} 条资产数据", assets.size());
            return R.success(assets.size());
        } catch (Exception e) {
            logger.error("导入资产失败", e);
            return R.error("导入资产失败: " + e.getMessage());
        }
    }
}
