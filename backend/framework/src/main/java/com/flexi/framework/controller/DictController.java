package com.flexi.framework.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flexi.framework.entity.Dict;
import com.flexi.framework.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Dict> dictPage = dictService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", dictPage.getRecords());
        response.put("total", dictPage.getTotal());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody Dict dict) {
        dictService.save(dict);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "添加成功");
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> update(@RequestBody Dict dict) {
        dictService.updateById(dict);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        dictService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dict> getById(@PathVariable Long id) {
        Dict dict = dictService.getById(id);
        return ResponseEntity.ok(dict);
    }
}