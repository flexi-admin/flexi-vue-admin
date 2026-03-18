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
@RequestMapping("/asset/approval")
public class AssetApprovalController {

    @Autowired
    private AssetApplyService assetApplyService;

    @RequirePermission("asset:approval:list")
    @GetMapping
    public R<com.baomidou.mybatisplus.core.metadata.IPage<AssetApplyDTO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return R.success(assetApplyService.listPendingApplies(page, size));
    }

    @RequirePermission("asset:approval:operate")
    @PostMapping("/process/{id}")
    public R<?> process(@PathVariable Long id, @RequestParam String action, @RequestParam(required = false) String reason) {
        AssetApply assetApply = assetApplyService.getById(id);
        if (assetApply == null) {
            return R.error("申请记录不存在");
        }
        
        if ("approve".equals(action)) {
            assetApply.setStatus("approved");
        } else if ("reject".equals(action)) {
            assetApply.setStatus("rejected");
            assetApply.setRejectReason(reason);
        } else {
            return R.error("无效的操作类型");
        }
        
        assetApply.setApprovalTime(System.currentTimeMillis());
        assetApply.setApproverId(SecurityUtils.getCurrentUserId());
        assetApplyService.updateById(assetApply);
        return R.success();
    }


}