package com.flexi.app.controller;

import com.flexi.app.entity.AssetApply;
import com.flexi.app.entity.AssetApplyDTO;
import com.flexi.app.service.AssetApplyService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import io.github.zmxckj.flexiadmin.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asset/apply")
public class AssetApplyController {

    @Autowired
    private AssetApplyService assetApplyService;

    @RequirePermission("asset:apply:list")
    @GetMapping
    public R<com.baomidou.mybatisplus.core.metadata.IPage<AssetApplyDTO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return R.success(assetApplyService.listWithDetails(page, size));
    }

    @RequirePermission("asset:apply:list")
    @GetMapping("/my")
    public R<com.baomidou.mybatisplus.core.metadata.IPage<AssetApplyDTO>> listMyApplies(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.success(assetApplyService.listMyApplies(page, size, userId));
    }

    @RequirePermission("asset:apply:add")
    @PostMapping
    public R<?> add(@RequestBody AssetApply assetApply) {
        assetApply.setApplyTime(System.currentTimeMillis());
        assetApply.setStatus("pending");
        assetApply.setCreateTime(System.currentTimeMillis());
        assetApply.setUpdateTime(System.currentTimeMillis());
        assetApplyService.save(assetApply);
        return R.success();
    }

    @RequirePermission("asset:apply:edit")
    @PutMapping
    public R<?> edit(@RequestBody AssetApply assetApply) {
        assetApply.setUpdateTime(System.currentTimeMillis());
        if ("approved".equals(assetApply.getStatus()) || "rejected".equals(assetApply.getStatus())) {
            assetApply.setApprovalTime(System.currentTimeMillis());
        }
        assetApplyService.updateById(assetApply);
        return R.success();
    }

    @RequirePermission("asset:apply:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        assetApplyService.removeById(id);
        return R.success();
    }
}