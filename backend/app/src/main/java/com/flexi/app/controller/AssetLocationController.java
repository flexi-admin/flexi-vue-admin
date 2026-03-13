package com.flexi.app.controller;

import com.flexi.app.entity.AssetLocation;
import com.flexi.app.entity.AssetLocationTree;
import com.flexi.app.service.AssetLocationService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asset-location")
public class AssetLocationController {

    @Autowired
    private AssetLocationService assetLocationService;

    @RequirePermission("asset:location:list")
    @GetMapping("/tree")
    public R<List<AssetLocationTree>> tree() {
        return R.success(assetLocationService.tree());
    }

    @RequirePermission("asset:location:list")
    @GetMapping
    public R<List<AssetLocation>> list() {
        return R.success(assetLocationService.list());
    }

    @RequirePermission("asset:location:add")
    @PostMapping
    public R<?> add(@RequestBody AssetLocation assetLocation) {
        assetLocation.setCreateTime(System.currentTimeMillis());
        assetLocation.setUpdateTime(System.currentTimeMillis());
        assetLocationService.save(assetLocation);
        return R.success();
    }

    @RequirePermission("asset:location:edit")
    @PutMapping
    public R<?> edit(@RequestBody AssetLocation assetLocation) {
        assetLocation.setUpdateTime(System.currentTimeMillis());
        assetLocationService.updateById(assetLocation);
        return R.success();
    }

    @RequirePermission("asset:location:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        assetLocationService.removeById(id);
        return R.success();
    }
}