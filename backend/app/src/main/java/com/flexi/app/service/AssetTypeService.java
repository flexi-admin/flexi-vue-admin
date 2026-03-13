package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.AssetType;
import com.flexi.app.entity.AssetTypeTree;
import java.util.List;

public interface AssetTypeService extends IService<AssetType> {
    List<AssetTypeTree> tree();
}