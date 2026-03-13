package io.github.zmxckj.flexiadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.Config;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.service.ConfigService;
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
    public R<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Config> configPage = configService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", configPage.getRecords());
        response.put("total", configPage.getTotal());
        return R.success(response);
    }

    @PostMapping
    public R<?> add(@RequestBody Config config) {
        configService.save(config);
        return R.success();
    }

    @PutMapping
    public R<?> update(@RequestBody Config config) {
        configService.updateById(config);
        return R.success();
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        configService.removeById(id);
        return R.success();
    }

    @GetMapping("/{id}")
    public R<Config> getById(@PathVariable Long id) {
        Config config = configService.getById(id);
        return R.success(config);
    }

    @GetMapping("/all")
    public R<Map<String, String>> getAll() {
        Map<String, String> configMap = new HashMap<>();
        for (Config config : configService.list()) {
            configMap.put(config.getConfigKey(), config.getValue());
        }
        return R.success(configMap);
    }
}