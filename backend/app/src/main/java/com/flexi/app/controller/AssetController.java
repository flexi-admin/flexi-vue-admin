package com.flexi.app.controller;

import com.flexi.app.entity.Asset;
import com.flexi.app.entity.AssetDTO;
import com.flexi.app.service.AssetService;
import com.flexi.app.utils.SnowflakeIdGenerator;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import io.github.zmxckj.flexiadmin.security.SecurityUtils;
import io.github.zmxckj.flexiadmin.utils.DataScopeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/asset")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @RequirePermission("asset:list")
    @GetMapping
    public R<com.baomidou.mybatisplus.core.metadata.IPage<AssetDTO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long typeId) {
        return R.success(assetService.listWithDetails(page, size, name, status, typeId));
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
        
        asset.setCreateTime(System.currentTimeMillis());
        asset.setUpdateTime(System.currentTimeMillis());
        asset.setIsDeleted(0);
        return assetService.save(asset);
    }

    @RequirePermission("asset:edit")
    @PutMapping
    public R<?> edit(@RequestBody Asset asset) {
        asset.setUpdateTime(System.currentTimeMillis());
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

    // 统计相关API
    @RequirePermission("asset:query")
    @GetMapping("/statistics")
    public R<java.util.Map<String, Object>> getAssetStatistics() {
        return R.success(assetService.getAssetStatistics());
    }

    @RequirePermission("asset:query")
    @GetMapping("/type-distribution")
    public R<List<java.util.Map<String, Object>>> getAssetTypeDistribution() {
        return R.success(assetService.getAssetTypeDistribution());
    }

    @RequirePermission("asset:query")
    @GetMapping("/status-distribution")
    public R<List<java.util.Map<String, Object>>> getAssetStatusDistribution() {
        return R.success(assetService.getAssetStatusDistribution());
    }

    @RequirePermission("asset:query")
    @GetMapping("/status-distribution-by-type")
    public R<List<java.util.Map<String, Object>>> getAssetStatusDistributionByType(@RequestParam String type) {
        return R.success(assetService.getAssetStatusDistributionByType(type));
    }

    @RequirePermission("asset:query")
    @GetMapping("/idle-rate-analysis")
    public R<List<java.util.Map<String, Object>>> getIdleRateAnalysis() {
        return R.success(assetService.getIdleRateAnalysis());
    }

    @RequirePermission("asset:query")
    @GetMapping("/amount-statistics")
    public R<List<java.util.Map<String, Object>>> getAmountStatistics() {
        return R.success(assetService.getAmountStatistics());
    }




}