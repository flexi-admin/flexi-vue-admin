package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.Asset;
import com.flexi.app.entity.AssetDTO;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;

public interface AssetService extends IService<Asset> {
    List<AssetDTO> listWithDetails();
    IPage<AssetDTO> listWithDetails(Integer page, Integer size);
    IPage<AssetDTO> listMyAssets(Integer page, Integer size, Long userId);
}