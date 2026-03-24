package com.flexi.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flexi.app.entity.AssetSupplier;
import com.flexi.app.service.AssetSupplierService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.core.annotation.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asset-supplier")
public class AssetSupplierController {

    @Autowired
    private AssetSupplierService assetSupplierService;

    @RequirePermission("asset:supplier:list")
    @GetMapping
    public R<List<AssetSupplier>> list() {
        QueryWrapper<AssetSupplier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        return R.success(assetSupplierService.list(queryWrapper));
    }

    @RequirePermission("asset:supplier:add")
    @PostMapping
    public R<?> add(@RequestBody AssetSupplier assetSupplier) {
        assetSupplier.setIsDeleted(0);
        assetSupplier.setCreateTime(System.currentTimeMillis());
        assetSupplier.setUpdateTime(System.currentTimeMillis());
        assetSupplierService.save(assetSupplier);
        return R.success();
    }

    @RequirePermission("asset:supplier:edit")
    @PutMapping
    public R<?> update(@RequestBody AssetSupplier assetSupplier) {
        assetSupplier.setUpdateTime(System.currentTimeMillis());
        assetSupplierService.updateById(assetSupplier);
        return R.success();
    }

    @RequirePermission("asset:supplier:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        AssetSupplier assetSupplier = new AssetSupplier();
        assetSupplier.setId(id);
        assetSupplier.setIsDeleted(1);
        assetSupplier.setUpdateTime(System.currentTimeMillis());
        assetSupplierService.updateById(assetSupplier);
        return R.success();
    }

    @RequirePermission("asset:supplier:list")
    @GetMapping("/{id}")
    public R<AssetSupplier> getById(@PathVariable Long id) {
        return R.success(assetSupplierService.getById(id));
    }
}
