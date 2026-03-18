package io.github.zmxckj.flexiadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.OperationLog;
import io.github.zmxckj.flexiadmin.entity.LoginLog;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import io.github.zmxckj.flexiadmin.service.OperationLogService;
import io.github.zmxckj.flexiadmin.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private LoginLogService loginLogService;

    // 操作日志相关接口
    @RequirePermission("log:list")
    @GetMapping("/operation/list")
    public R<Map<String, Object>> operationList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<OperationLog> operationLogPage = operationLogService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", operationLogPage.getRecords());
        response.put("total", operationLogPage.getTotal());
        return R.success(response);
    }

    // 登录日志相关接口
    @RequirePermission("log:list")
    @GetMapping("/login/list")
    public R<Map<String, Object>> loginList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<LoginLog> loginLogPage = loginLogService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", loginLogPage.getRecords());
        response.put("total", loginLogPage.getTotal());
        return R.success(response);
    }

    @DeleteMapping("/operation/{id}")
    public R<?> deleteOperationLog(@PathVariable Long id) {
        operationLogService.removeById(id);
        return R.success();
    }

    @DeleteMapping("/login/{id}")
    public R<?> deleteLoginLog(@PathVariable Long id) {
        loginLogService.removeById(id);
        return R.success();
    }
}