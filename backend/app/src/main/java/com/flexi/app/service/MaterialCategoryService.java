package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.MaterialCategory;
import com.flexi.app.entity.MaterialCategoryTree;
import java.util.List;

public interface MaterialCategoryService extends IService<MaterialCategory> {
    List<MaterialCategoryTree> tree();
}
