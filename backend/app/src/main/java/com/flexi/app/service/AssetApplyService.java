package com.flexi.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.app.entity.AssetApply;
import com.flexi.app.entity.AssetApplyDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface AssetApplyService extends IService<AssetApply> {
    IPage<AssetApplyDTO> listWithDetails(Integer page, Integer size);
    IPage<AssetApplyDTO> listMyApplies(Integer page, Integer size, Long userId);
    IPage<AssetApplyDTO> listPendingApplies(Integer page, Integer size);
}