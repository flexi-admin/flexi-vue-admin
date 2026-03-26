package com.flexi.app.controller;

import com.flexi.app.entity.MaterialLocation;
import com.flexi.app.service.MaterialLocationService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material-location")
public class MaterialLocationController {

    @Autowired
    private MaterialLocationService materialLocationService;

    @RequirePermission("material:warehouse:list")
    @GetMapping
    public R<List<MaterialLocation>> list() {
        return R.success(materialLocationService.list());
    }

    @RequirePermission("material:warehouse:list")
    @GetMapping("/warehouse/{warehouseId}")
    public R<List<MaterialLocation>> getByWarehouseId(@PathVariable Long warehouseId) {
        return R.success(materialLocationService.getByWarehouseId(warehouseId));
    }

    @RequirePermission("material:warehouse:add")
    @PostMapping
    public R<?> add(@RequestBody MaterialLocation materialLocation) {
        materialLocation.setCreateTime(System.currentTimeMillis());
        materialLocation.setUpdateTime(System.currentTimeMillis());
        materialLocationService.save(materialLocation);
        return R.success();
    }

    @RequirePermission("material:warehouse:edit")
    @PutMapping
    public R<?> edit(@RequestBody MaterialLocation materialLocation) {
        materialLocation.setUpdateTime(System.currentTimeMillis());
        materialLocationService.updateById(materialLocation);
        return R.success();
    }

    @RequirePermission("material:warehouse:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        materialLocationService.removeById(id);
        return R.success();
    }
}
