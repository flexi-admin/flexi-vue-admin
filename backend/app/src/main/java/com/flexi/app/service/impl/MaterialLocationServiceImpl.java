package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.MaterialLocation;
import com.flexi.app.mapper.MaterialLocationMapper;
import com.flexi.app.service.MaterialLocationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialLocationServiceImpl extends ServiceImpl<MaterialLocationMapper, MaterialLocation> implements MaterialLocationService {
    @Override
    public List<MaterialLocation> getByWarehouseId(Long warehouseId) {
        QueryWrapper<MaterialLocation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("warehouse_id", warehouseId);
        return list(queryWrapper);
    }
}
