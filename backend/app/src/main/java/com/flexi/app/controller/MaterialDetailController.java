package com.flexi.app.controller;

import com.flexi.app.entity.MaterialDetail;
import com.flexi.app.service.MaterialDetailService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material-detail")
public class MaterialDetailController {

    @Autowired
    private MaterialDetailService materialDetailService;

    @RequirePermission("material:list")
    @GetMapping
    public R<List<MaterialDetail>> list() {
        return R.success(materialDetailService.list());
    }

    @RequirePermission("material:list")
    @GetMapping("/material/{materialId}")
    public R<List<MaterialDetail>> getByMaterialId(@PathVariable Long materialId) {
        return R.success(materialDetailService.getByMaterialId(materialId));
    }

    @RequirePermission("material:add")
    @PostMapping
    public R<?> add(@RequestBody MaterialDetail materialDetail) {
        materialDetail.setCreateTime(System.currentTimeMillis());
        materialDetail.setUpdateTime(System.currentTimeMillis());
        materialDetailService.save(materialDetail);
        return R.success();
    }

    @RequirePermission("material:edit")
    @PutMapping
    public R<?> edit(@RequestBody MaterialDetail materialDetail) {
        materialDetail.setUpdateTime(System.currentTimeMillis());
        materialDetailService.updateById(materialDetail);
        return R.success();
    }

    @RequirePermission("material:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        materialDetailService.removeById(id);
        return R.success();
    }
}
