package com.flexi.app.controller;

import com.flexi.app.entity.MaterialWarehouse;
import com.flexi.app.service.MaterialWarehouseService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material-warehouse")
public class MaterialWarehouseController {

    @Autowired
    private MaterialWarehouseService materialWarehouseService;

    @RequirePermission("material:warehouse:list")
    @GetMapping
    public R<List<MaterialWarehouse>> list() {
        return R.success(materialWarehouseService.list());
    }

    @RequirePermission("material:warehouse:add")
    @PostMapping
    public R<?> add(@RequestBody MaterialWarehouse materialWarehouse) {
        materialWarehouse.setCreateTime(System.currentTimeMillis());
        materialWarehouse.setUpdateTime(System.currentTimeMillis());
        materialWarehouseService.save(materialWarehouse);
        return R.success();
    }

    @RequirePermission("material:warehouse:edit")
    @PutMapping
    public R<?> edit(@RequestBody MaterialWarehouse materialWarehouse) {
        materialWarehouse.setUpdateTime(System.currentTimeMillis());
        materialWarehouseService.updateById(materialWarehouse);
        return R.success();
    }

    @RequirePermission("material:warehouse:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        materialWarehouseService.removeById(id);
        return R.success();
    }
}
