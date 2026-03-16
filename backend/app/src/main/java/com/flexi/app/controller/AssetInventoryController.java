package com.flexi.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flexi.app.entity.AssetInventory;
import com.flexi.app.service.AssetInventoryService;
import io.github.zmxckj.flexiadmin.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asset/inventory")
public class AssetInventoryController {

    @Autowired
    private AssetInventoryService assetInventoryService;

    // 分页查询
    @GetMapping("/list")
    public R<Page<AssetInventory>> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return R.success(assetInventoryService.page(new Page<>(page, size)));
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public R<AssetInventory> getById(@PathVariable Long id) {
        return R.success(assetInventoryService.getById(id));
    }

    // 新增
    @PostMapping
    public R<?> save(@RequestBody AssetInventory assetInventory) {
        assetInventoryService.save(assetInventory);
        return R.success();
    }

    // 更新
    @PutMapping
    public R<?> update(@RequestBody AssetInventory assetInventory) {
        assetInventoryService.updateById(assetInventory);
        return R.success();
    }

    // 删除
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        assetInventoryService.removeById(id);
        return R.success();
    }
}
