package io.github.zmxckj.flexiadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.Task;
import io.github.zmxckj.flexiadmin.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Task> taskPage = taskService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", taskPage.getRecords());
        response.put("total", taskPage.getTotal());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody Task task) {
        taskService.save(task);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "添加成功");
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> update(@RequestBody Task task) {
        taskService.updateById(task);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        taskService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id) {
        Task task = taskService.getById(id);
        return ResponseEntity.ok(task);
    }
}