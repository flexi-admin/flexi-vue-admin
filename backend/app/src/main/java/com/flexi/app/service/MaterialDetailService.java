package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.MaterialDetail;

import java.util.List;

public interface MaterialDetailService extends IService<MaterialDetail> {
    List<MaterialDetail> getByMaterialId(Long materialId);
}
