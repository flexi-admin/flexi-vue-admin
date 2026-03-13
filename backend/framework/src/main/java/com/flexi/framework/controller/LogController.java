package com.flexi.framework.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flexi.framework.entity.OperationLog;
import com.flexi.framework.entity.LoginLog;
import com.flexi.framework.service.OperationLogService;
import com.flexi.framework.service.LoginLogService;
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
    @GetMapping("/operation/list")
    public ResponseEntity<Map<String, Object>> operationList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<OperationLog> operationLogPage = operationLogService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", operationLogPage.getRecords());
        response.put("total", operationLogPage.getTotal());
        return ResponseEntity.ok(response);
    }

    // 登录日志相关接口
    @GetMapping("/login/list")
    public ResponseEntity<Map<String, Object>> loginList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<LoginLog> loginLogPage = loginLogService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", loginLogPage.getRecords());
        response.put("total", loginLogPage.getTotal());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/operation/{id}")
    public ResponseEntity<Map<String, Object>> deleteOperationLog(@PathVariable Long id) {
        operationLogService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/login/{id}")
    public ResponseEntity<Map<String, Object>> deleteLoginLog(@PathVariable Long id) {
        loginLogService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}