package io.github.zmxckj.flexiadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.Task;
import io.github.zmxckj.flexiadmin.common.R;
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
    public R<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Task> taskPage = taskService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", taskPage.getRecords());
        response.put("total", taskPage.getTotal());
        return R.success(response);
    }

    @PostMapping
    public R<?> add(@RequestBody Task task) {
        taskService.save(task);
        return R.success();
    }

    @PutMapping
    public R<?> update(@RequestBody Task task) {
        taskService.updateById(task);
        return R.success();
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        taskService.removeById(id);
        return R.success();
    }

    @GetMapping("/{id}")
    public R<Task> getById(@PathVariable Long id) {
        Task task = taskService.getById(id);
        return R.success(task);
    }
}