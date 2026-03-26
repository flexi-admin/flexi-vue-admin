package com.flexi.app.controller;

import com.flexi.app.entity.MaterialCategory;
import com.flexi.app.entity.MaterialCategoryTree;
import com.flexi.app.service.MaterialCategoryService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material-category")
public class MaterialCategoryController {

    @Autowired
    private MaterialCategoryService materialCategoryService;

    @RequirePermission("material:category:list")
    @GetMapping("/tree")
    public R<List<MaterialCategoryTree>> tree() {
        return R.success(materialCategoryService.tree());
    }

    @RequirePermission("material:category:list")
    @GetMapping
    public R<List<MaterialCategory>> list() {
        return R.success(materialCategoryService.list());
    }

    @RequirePermission("material:category:add")
    @PostMapping
    public R<?> add(@RequestBody MaterialCategory materialCategory) {
        materialCategory.setCreateTime(System.currentTimeMillis());
        materialCategory.setUpdateTime(System.currentTimeMillis());
        materialCategoryService.save(materialCategory);
        return R.success();
    }

    @RequirePermission("material:category:edit")
    @PutMapping
    public R<?> edit(@RequestBody MaterialCategory materialCategory) {
        materialCategory.setUpdateTime(System.currentTimeMillis());
        materialCategoryService.updateById(materialCategory);
        return R.success();
    }

    @RequirePermission("material:category:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        materialCategoryService.removeById(id);
        return R.success();
    }
}
