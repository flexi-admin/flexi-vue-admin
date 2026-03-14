package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.Asset;
import com.flexi.app.entity.AssetDTO;

import java.util.List;

public interface AssetService extends IService<Asset> {
    List<AssetDTO> listWithDetails();
}