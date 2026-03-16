package com.flexi.app.controller;

import com.flexi.app.entity.Asset;
import com.flexi.app.entity.AssetDTO;
import com.flexi.app.service.AssetService;
import com.flexi.app.utils.SnowflakeIdGenerator;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asset")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @RequirePermission("asset:list")
    @GetMapping
    public R<com.baomidou.mybatisplus.core.metadata.IPage<AssetDTO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return R.success(assetService.listWithDetails(page, size));
    }

    @RequirePermission("asset:add")
    @PostMapping
    public boolean add(@RequestBody Asset asset) {
        // 生成资产编码（使用雪花算法生成全局唯一ID）
        String uuid = cn.hutool.core.util.IdUtil.objectId();
        asset.setCode(uuid);
        // 用资产编码的值赋值给标签编码
        asset.setLabelCode(uuid);
        
        asset.setCreateTime(System.currentTimeMillis());
        asset.setUpdateTime(System.currentTimeMillis());
        asset.setIsDeleted(0);
        return assetService.save(asset);
    }

    @RequirePermission("asset:edit")
    @PutMapping
    public R<?> edit(@RequestBody Asset asset) {
        asset.setUpdateTime(System.currentTimeMillis());
        assetService.updateById(asset);
        return R.success();
    }

    @RequirePermission("asset:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        assetService.removeById(id);
        return R.success();
    }
}