package com.flexi.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flexi.app.entity.AssetInventory;
import com.flexi.app.entity.AssetInventoryDetail;
import com.flexi.app.service.AssetInventoryService;
import com.flexi.app.service.AssetInventoryDetailService;
import io.github.zmxckj.flexiadmin.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import cn.hutool.core.util.IdUtil;

@RestController
@RequestMapping("/asset/inventory")
public class AssetInventoryController {

    @Autowired
    private AssetInventoryService assetInventoryService;

    @Autowired
    private AssetInventoryDetailService assetInventoryDetailService;

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
        // 生成盘点单号：PD+hutool的objectId
        String inventoryCode = "PD" + IdUtil.objectId();
        assetInventory.setInventoryCode(inventoryCode);
        assetInventory.setCreateTime(System.currentTimeMillis());
        assetInventory.setUpdateTime(System.currentTimeMillis());
        assetInventoryService.save(assetInventory);
        return R.success();
    }

    // 更新
    @PutMapping
    public R<?> update(@RequestBody AssetInventory assetInventory) {
        assetInventory.setUpdateTime(System.currentTimeMillis());
        assetInventoryService.updateById(assetInventory);
        return R.success();
    }

    // 删除
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        assetInventoryService.removeById(id);
        return R.success();
    }

    // 同步盘点明细
    @PostMapping("/sync")
    public R<?> sync(@RequestBody Map<String, Object> params) {
        Long inventoryId = Long.parseLong(params.get("inventoryId").toString());
        String inventoryType = params.get("inventoryType").toString();
        String inventoryDepts = params.get("inventoryDepts") != null ? params.get("inventoryDepts").toString() : "";
        String inventoryCategories = params.get("inventoryCategories") != null ? params.get("inventoryCategories").toString() : "";
        assetInventoryService.syncInventory(inventoryId, inventoryType, inventoryDepts, inventoryCategories);
        return R.success();
    }

    // 下发盘点
    @GetMapping("/issue/{id}")
    public R<?> issue(@PathVariable Long id) {
        return R.success(assetInventoryService.issueInventory(id));
    }

    // 接收盘点数据
    @PostMapping("/save-details")
    public R<Boolean> saveInventoryDetails(@RequestBody List<AssetInventoryDetail> details) {

        System.out.println(details);
        boolean result = false;

        //对于接口接收的details列表，只保留id不为空的，并且id在盘点明细id列表中的那些数据
        List<AssetInventoryDetail> validDetails = details.stream()
                .filter(detail -> detail.getId() != null)
                .collect(java.util.stream.Collectors.toList());
        
        //批量更新筛选后的数据
        if (!validDetails.isEmpty()) {
            result = assetInventoryDetailService.updateBatchDetails(validDetails);
        }

        return R.success(result);
    }
}
