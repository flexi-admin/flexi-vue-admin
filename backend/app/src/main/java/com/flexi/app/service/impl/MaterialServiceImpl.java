package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.Material;
import com.flexi.app.mapper.MaterialMapper;
import com.flexi.app.service.MaterialService;
import org.springframework.stereotype.Service;

@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {
}
