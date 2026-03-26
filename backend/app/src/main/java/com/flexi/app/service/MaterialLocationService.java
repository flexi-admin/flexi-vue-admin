package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.MaterialLocation;

import java.util.List;

public interface MaterialLocationService extends IService<MaterialLocation> {
    List<MaterialLocation> getByWarehouseId(Long warehouseId);
}
