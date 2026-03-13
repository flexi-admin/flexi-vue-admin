package com.flexi.framework.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flexi.framework.entity.Config;
import com.flexi.framework.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Config> configPage = configService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", configPage.getRecords());
        response.put("total", configPage.getTotal());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody Config config) {
        configService.save(config);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "添加成功");
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> update(@RequestBody Config config) {
        configService.updateById(config);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        configService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Config> getById(@PathVariable Long id) {
        Config config = configService.getById(id);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, String>> getAll() {
        Map<String, String> configMap = new HashMap<>();
        for (Config config : configService.list()) {
            configMap.put(config.getConfigKey(), config.getValue());
        }
        return ResponseEntity.ok(configMap);
    }
}