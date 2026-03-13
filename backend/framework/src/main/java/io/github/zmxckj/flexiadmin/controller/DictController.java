package io.github.zmxckj.flexiadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.Dict;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.service.DictService;
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
    public R<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Dict> dictPage = dictService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", dictPage.getRecords());
        response.put("total", dictPage.getTotal());
        return R.success(response);
    }

    @PostMapping
    public R<?> add(@RequestBody Dict dict) {
        dictService.save(dict);
        return R.success();
    }

    @PutMapping
    public R<?> update(@RequestBody Dict dict) {
        dictService.updateById(dict);
        return R.success();
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        dictService.removeById(id);
        return R.success();
    }

    @GetMapping("/{id}")
    public R<Dict> getById(@PathVariable Long id) {
        Dict dict = dictService.getById(id);
        return R.success(dict);
    }
}