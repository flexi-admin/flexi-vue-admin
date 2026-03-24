package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.Asset;
import com.flexi.app.entity.AssetDTO;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;

public interface AssetService extends IService<Asset> {
    List<AssetDTO> listWithDetails();
    IPage<AssetDTO> listWithDetails(Integer page, Integer size);
    IPage<AssetDTO> listWithDetails(Integer page, Integer size, String name, String status, Long typeId);
    IPage<AssetDTO> listMyAssets(Integer page, Integer size, Long userId);
    List<Asset> listAssetsWithoutLabelCode();
    void updateLabelCode(java.util.Map<String, String> codeLabelMap);
    AssetDTO getAssetById(Long id);
    List<java.util.Map<String, String>> getBatchPrintData();
    
    // 统计相关方法
    java.util.Map<String, Object> getAssetStatistics();
    List<java.util.Map<String, Object>> getAssetTypeDistribution();
    List<java.util.Map<String, Object>> getAssetStatusDistribution();
    List<java.util.Map<String, Object>> getAssetStatusDistributionByType(String type);
    List<java.util.Map<String, Object>> getIdleRateAnalysis();
    List<java.util.Map<String, Object>> getAmountStatistics();
}