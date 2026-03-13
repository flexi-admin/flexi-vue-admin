package com.flexi.app.controller;

import com.flexi.app.entity.AssetType;
import com.flexi.app.entity.AssetTypeTree;
import com.flexi.app.service.AssetTypeService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asset-type")
public class AssetTypeController {

    @Autowired
    private AssetTypeService assetTypeService;

    @RequirePermission("asset:type:list")
    @GetMapping("/tree")
    public R<List<AssetTypeTree>> tree() {
        return R.success(assetTypeService.tree());
    }

    @RequirePermission("asset:type:list")
    @GetMapping
    public R<List<AssetType>> list() {
        return R.success(assetTypeService.list());
    }

    @RequirePermission("asset:type:add")
    @PostMapping
    public R<?> add(@RequestBody AssetType assetType) {
        assetType.setCreateTime(System.currentTimeMillis());
        assetType.setUpdateTime(System.currentTimeMillis());
        assetTypeService.save(assetType);
        return R.success();
    }

    @RequirePermission("asset:type:edit")
    @PutMapping
    public R<?> edit(@RequestBody AssetType assetType) {
        assetType.setUpdateTime(System.currentTimeMillis());
        assetTypeService.updateById(assetType);
        return R.success();
    }

    @RequirePermission("asset:type:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        assetTypeService.removeById(id);
        return R.success();
    }
}