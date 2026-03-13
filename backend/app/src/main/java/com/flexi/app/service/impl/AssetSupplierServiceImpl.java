package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.AssetSupplier;
import com.flexi.app.mapper.AssetSupplierMapper;
import com.flexi.app.service.AssetSupplierService;
import org.springframework.stereotype.Service;

@Service
public class AssetSupplierServiceImpl extends ServiceImpl<AssetSupplierMapper, AssetSupplier> implements AssetSupplierService {
}
