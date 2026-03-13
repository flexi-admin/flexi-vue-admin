package com.flexi.app.controller;

import com.flexi.app.entity.AssetLocation;
import com.flexi.app.entity.AssetLocationTree;
import com.flexi.app.service.AssetLocationService;
import io.github.flexiadmin.framework.security.RequirePermission;
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
    public List<AssetLocationTree> tree() {
        return assetLocationService.tree();
    }

    @RequirePermission("asset:location:list")
    @GetMapping
    public List<AssetLocation> list() {
        return assetLocationService.list();
    }

    @RequirePermission("asset:location:add")
    @PostMapping
    public boolean add(@RequestBody AssetLocation assetLocation) {
        assetLocation.setCreateTime(System.currentTimeMillis());
        assetLocation.setUpdateTime(System.currentTimeMillis());
        return assetLocationService.save(assetLocation);
    }

    @RequirePermission("asset:location:edit")
    @PutMapping
    public boolean edit(@RequestBody AssetLocation assetLocation) {
        assetLocation.setUpdateTime(System.currentTimeMillis());
        return assetLocationService.updateById(assetLocation);
    }

    @RequirePermission("asset:location:delete")
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return assetLocationService.removeById(id);
    }
}