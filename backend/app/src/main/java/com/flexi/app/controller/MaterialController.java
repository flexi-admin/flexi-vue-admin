package com.flexi.app.controller;

import com.flexi.app.entity.Material;
import com.flexi.app.service.MaterialService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @RequirePermission("material:list")
    @GetMapping
    public R<List<Material>> list() {
        return R.success(materialService.list());
    }

    @RequirePermission("material:add")
    @PostMapping
    public R<?> add(@RequestBody Material material) {
        material.setCreateTime(System.currentTimeMillis());
        material.setUpdateTime(System.currentTimeMillis());
        materialService.save(material);
        return R.success();
    }

    @RequirePermission("material:edit")
    @PutMapping
    public R<?> edit(@RequestBody Material material) {
        material.setUpdateTime(System.currentTimeMillis());
        materialService.updateById(material);
        return R.success();
    }

    @RequirePermission("material:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        materialService.removeById(id);
        return R.success();
    }
}
