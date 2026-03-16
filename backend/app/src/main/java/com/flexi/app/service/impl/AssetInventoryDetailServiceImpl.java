package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.AssetInventoryDetail;
import com.flexi.app.mapper.AssetInventoryDetailMapper;
import com.flexi.app.service.AssetInventoryDetailService;
import org.springframework.stereotype.Service;

@Service
public class AssetInventoryDetailServiceImpl extends ServiceImpl<AssetInventoryDetailMapper, AssetInventoryDetail> implements AssetInventoryDetailService {
}
