package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.AssetLocation;
import com.flexi.app.entity.AssetLocationTree;
import java.util.List;

public interface AssetLocationService extends IService<AssetLocation> {
    List<AssetLocationTree> tree();
}