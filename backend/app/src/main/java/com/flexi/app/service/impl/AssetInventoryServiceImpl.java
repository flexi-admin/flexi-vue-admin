package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.AssetInventory;
import com.flexi.app.mapper.AssetInventoryMapper;
import com.flexi.app.service.AssetInventoryService;
import org.springframework.stereotype.Service;

@Service
public class AssetInventoryServiceImpl extends ServiceImpl<AssetInventoryMapper, AssetInventory> implements AssetInventoryService {
}
