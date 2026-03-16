package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.AssetInventory;

import java.util.Map;

public interface AssetInventoryService extends IService<AssetInventory> {
    void syncInventory(Long inventoryId, String inventoryType, String inventoryDepts, String inventoryCategories);
    Map<String, Object> issueInventory(Long inventoryId);
}
