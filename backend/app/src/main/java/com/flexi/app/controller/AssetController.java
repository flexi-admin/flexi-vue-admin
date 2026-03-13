package com.flexi.app.controller;

import com.flexi.app.entity.Asset;
import com.flexi.app.service.AssetService;
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
    public List<Asset> list() {
        return assetService.list();
    }

    @RequirePermission("asset:add")
    @PostMapping
    public boolean add(@RequestBody Asset asset) {
        asset.setCreateTime(System.currentTimeMillis());
        asset.setUpdateTime(System.currentTimeMillis());
        asset.setIsDeleted(0);
        return assetService.save(asset);
    }

    @RequirePermission("asset:edit")
    @PutMapping
    public boolean edit(@RequestBody Asset asset) {
        asset.setUpdateTime(System.currentTimeMillis());
        return assetService.updateById(asset);
    }

    @RequirePermission("asset:delete")
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return assetService.removeById(id);
    }
}