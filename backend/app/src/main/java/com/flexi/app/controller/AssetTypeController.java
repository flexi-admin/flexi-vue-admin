package com.flexi.app.controller;

import com.flexi.app.entity.AssetType;
import com.flexi.app.entity.AssetTypeTree;
import com.flexi.app.service.AssetTypeService;
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
    public List<AssetTypeTree> tree() {
        return assetTypeService.tree();
    }

    @RequirePermission("asset:type:list")
    @GetMapping
    public List<AssetType> list() {
        return assetTypeService.list();
    }

    @RequirePermission("asset:type:add")
    @PostMapping
    public boolean add(@RequestBody AssetType assetType) {
        assetType.setCreateTime(System.currentTimeMillis());
        assetType.setUpdateTime(System.currentTimeMillis());
        return assetTypeService.save(assetType);
    }

    @RequirePermission("asset:type:edit")
    @PutMapping
    public boolean edit(@RequestBody AssetType assetType) {
        assetType.setUpdateTime(System.currentTimeMillis());
        return assetTypeService.updateById(assetType);
    }

    @RequirePermission("asset:type:delete")
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return assetTypeService.removeById(id);
    }
}