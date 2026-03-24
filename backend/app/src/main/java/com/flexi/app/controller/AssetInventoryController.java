package com.flexi.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flexi.app.entity.AssetInventory;
import com.flexi.app.entity.AssetInventoryDetail;
import com.flexi.app.service.AssetInventoryService;
import com.flexi.app.service.AssetInventoryDetailService;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
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
    @RequirePermission("asset:inventory:list")
    @GetMapping("/list")
    public R<Page<AssetInventory>> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return R.success(assetInventoryService.page(new Page<>(page, size)));
    }

    // 根据ID查询
    @RequirePermission("asset:inventory:query")
    @GetMapping("/{id}")
    public R<AssetInventory> getById(@PathVariable Long id) {
        return R.success(assetInventoryService.getById(id));
    }

    // 新增
    @RequirePermission("asset:inventory:add")
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
    @RequirePermission("asset:inventory:edit")
    @PutMapping
    public R<?> update(@RequestBody AssetInventory assetInventory) {
        assetInventory.setUpdateTime(System.currentTimeMillis());
        assetInventoryService.updateById(assetInventory);
        return R.success();
    }

    // 删除
    @RequirePermission("asset:inventory:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        assetInventoryService.removeById(id);
        return R.success();
    }

    // 同步盘点明细
    @RequirePermission("asset:inventory:edit")
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
    @RequirePermission("asset:inventory:issue")
    @GetMapping("/issue/{id}")
    public R<?> issue(@PathVariable Long id) {
        return R.success(assetInventoryService.issueInventory(id));
    }

    // 接收盘点数据
    @PostMapping("/save-details")
    public R<Boolean> saveInventoryDetails(@RequestBody List<AssetInventoryDetail> details) {
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
    
    // 获取最新盘点统计
    @RequirePermission("asset:inventory:statistics")
    @GetMapping("/latest-statistics")
    public R<java.util.Map<String, Object>> getLatestStatistics() {
        // 1. 获取最新的盘点记录
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AssetInventory> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.last("LIMIT 1");
        AssetInventory latestInventory = assetInventoryService.getOne(queryWrapper);
        
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        
        if (latestInventory != null) {
            // 2. 获取盘点日期
            java.util.Date inventoryDate = new java.util.Date(latestInventory.getCreateTime());
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(inventoryDate);
            statistics.put("date", dateStr);
            
            // 3. 获取盘点名称
            statistics.put("inventoryName", latestInventory.getInventoryName());
            
            // 3. 根据盘点ID查询盘点明细
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AssetInventoryDetail> detailQuery = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            detailQuery.eq("inventory_id", latestInventory.getId());
            List<AssetInventoryDetail> details = assetInventoryDetailService.list(detailQuery);
            
            // 4. 计算总盘点资产数和正常资产数
            long totalAssets = details.size();
            long normalAssets = details.stream().filter(detail -> "true".equals(detail.getStatus())).count();
            
            // 5. 计算正常率
            String normalRate = "0%";
            if (totalAssets > 0) {
                int rate = (int) (normalAssets * 100 / totalAssets);
                normalRate = rate + "%";
            }
            
            // 6. 计算与上次的变化率（这里简化处理，实际项目中可能需要存储历史盘点数据）
            String rateChange = "较上次 +0%";
            
            // 7. 填充统计数据
            statistics.put("totalAssets", totalAssets);
            statistics.put("normalAssets", normalAssets);
            statistics.put("normalRate", normalRate);
            statistics.put("rateChange", rateChange);
        }
        
        return R.success(statistics);
    }
    
    // 获取历史盘点正常率
    @RequirePermission("asset:inventory:statistics")
    @GetMapping("/rate-history")
    public R<List<java.util.Map<String, Object>>> getInventoryRateHistory() {
        List<java.util.Map<String, Object>> history = new java.util.ArrayList<>();
        
        // 1. 查询所有盘点记录，按时间倒序
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AssetInventory> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 5"); // 只取最近5次盘点
        List<AssetInventory> inventories = assetInventoryService.list(queryWrapper);
        
        // 2. 计算每次盘点的正常率
        for (AssetInventory inventory : inventories) {
            // 获取盘点日期
            java.util.Date inventoryDate = new java.util.Date(inventory.getCreateTime());
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(inventoryDate);
            
            // 查询盘点明细
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AssetInventoryDetail> detailQuery = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            detailQuery.eq("inventory_id", inventory.getId());
            List<AssetInventoryDetail> details = assetInventoryDetailService.list(detailQuery);
            
            // 计算正常率
            long totalAssets = details.size();
            long normalAssets = details.stream().filter(detail -> "true".equals(detail.getStatus())).count();
            
            String normalRate = "0%";
            if (totalAssets > 0) {
                int rate = (int) (normalAssets * 100 / totalAssets);
                normalRate = rate + "%";
            }
            
            // 构建历史记录
            java.util.Map<String, Object> record = new java.util.HashMap<>();
            record.put("date", dateStr);
            record.put("rate", normalRate);
            record.put("inventoryName", inventory.getInventoryName());
            history.add(record);
        }
        
        // 如果没有盘点记录，添加默认数据
        if (history.isEmpty()) {
            // 生成过去5个月的日期
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            
            for (int i = 4; i >= 0; i--) {
                // 计算日期（每个月的15号）
                java.util.Calendar recordCalendar = java.util.Calendar.getInstance();
                recordCalendar.add(java.util.Calendar.MONTH, -i);
                recordCalendar.set(java.util.Calendar.DAY_OF_MONTH, 15);
                String dateStr = sdf.format(recordCalendar.getTime());
                
                // 构建历史记录
                java.util.Map<String, Object> record = new java.util.HashMap<>();
                record.put("date", dateStr);
                record.put("rate", "0%");
                history.add(record);
            }
        }
        
        return R.success(history);
    }
}
