package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.MaterialWarehouse;
import com.flexi.app.mapper.MaterialWarehouseMapper;
import com.flexi.app.service.MaterialWarehouseService;
import org.springframework.stereotype.Service;

@Service
public class MaterialWarehouseServiceImpl extends ServiceImpl<MaterialWarehouseMapper, MaterialWarehouse> implements MaterialWarehouseService {
}
