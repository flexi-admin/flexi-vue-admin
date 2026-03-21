package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.AssetInventoryDetail;
import com.flexi.app.entity.AssetInventoryDetailDTO;

import java.util.List;

public interface AssetInventoryDetailService extends IService<AssetInventoryDetail> {
    Page<AssetInventoryDetailDTO> listByInventoryId(Long inventoryId, Page<AssetInventoryDetailDTO> page, String status);
    List<Long> getDetailIdsByInventoryId(Long inventoryId);
    boolean updateBatchDetails(List<AssetInventoryDetail> details);
}
