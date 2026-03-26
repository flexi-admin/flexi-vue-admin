package com.flexi.app.controller;

import com.flexi.app.entity.MaterialBrand;
import com.flexi.app.service.MaterialBrandService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material-brand")
public class MaterialBrandController {

    @Autowired
    private MaterialBrandService materialBrandService;

    @RequirePermission("material:brand:list")
    @GetMapping
    public R<List<MaterialBrand>> list() {
        return R.success(materialBrandService.list());
    }

    @RequirePermission("material:brand:add")
    @PostMapping
    public R<?> add(@RequestBody MaterialBrand materialBrand) {
        materialBrand.setCreateTime(System.currentTimeMillis());
        materialBrand.setUpdateTime(System.currentTimeMillis());
        materialBrandService.save(materialBrand);
        return R.success();
    }

    @RequirePermission("material:brand:edit")
    @PutMapping
    public R<?> edit(@RequestBody MaterialBrand materialBrand) {
        materialBrand.setUpdateTime(System.currentTimeMillis());
        materialBrandService.updateById(materialBrand);
        return R.success();
    }

    @RequirePermission("material:brand:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        materialBrandService.removeById(id);
        return R.success();
    }
}
