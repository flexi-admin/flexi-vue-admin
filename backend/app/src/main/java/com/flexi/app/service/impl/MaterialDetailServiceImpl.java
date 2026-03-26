package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.MaterialDetail;
import com.flexi.app.mapper.MaterialDetailMapper;
import com.flexi.app.service.MaterialDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialDetailServiceImpl extends ServiceImpl<MaterialDetailMapper, MaterialDetail> implements MaterialDetailService {
    @Override
    public List<MaterialDetail> getByMaterialId(Long materialId) {
        QueryWrapper<MaterialDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_id", materialId);
        return list(queryWrapper);
    }
}
