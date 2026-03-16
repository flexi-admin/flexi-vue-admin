package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.AssetInventoryDetail;
import com.flexi.app.entity.AssetInventoryDetailDTO;

public interface AssetInventoryDetailService extends IService<AssetInventoryDetail> {
    Page<AssetInventoryDetailDTO> listByInventoryId(Long inventoryId, Page<AssetInventoryDetailDTO> page, String status);
}
