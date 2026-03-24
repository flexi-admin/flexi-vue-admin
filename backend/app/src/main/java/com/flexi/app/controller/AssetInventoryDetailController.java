package com.flexi.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flexi.app.entity.AssetInventoryDetailDTO;
import com.flexi.app.service.AssetInventoryDetailService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asset/inventory/detail")
public class AssetInventoryDetailController {

    @Autowired
    private AssetInventoryDetailService assetInventoryDetailService;

    // 分页查询盘点明细
    @RequirePermission("asset:inventory:query")
    @GetMapping("/list")
    public R<Page<AssetInventoryDetailDTO>> list(
            @RequestParam Long inventoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        Page<AssetInventoryDetailDTO> detailPage = new Page<>(page, size);
        return R.success(assetInventoryDetailService.listByInventoryId(inventoryId, detailPage, status));
    }
}
