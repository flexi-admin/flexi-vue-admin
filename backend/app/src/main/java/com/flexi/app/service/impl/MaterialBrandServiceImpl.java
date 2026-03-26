package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.MaterialBrand;
import com.flexi.app.mapper.MaterialBrandMapper;
import com.flexi.app.service.MaterialBrandService;
import org.springframework.stereotype.Service;

@Service
public class MaterialBrandServiceImpl extends ServiceImpl<MaterialBrandMapper, MaterialBrand> implements MaterialBrandService {
}
