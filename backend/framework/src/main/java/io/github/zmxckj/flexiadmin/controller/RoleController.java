package io.github.zmxckj.flexiadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.Role;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import io.github.zmxckj.flexiadmin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequirePermission("role:list")
    @GetMapping("/list")
    public R<List<Role>> list() {
        List<Role> roles = roleService.findAll();
        return R.success(roles);
    }

    @RequirePermission("role:list")
    @GetMapping("/page")
    public R<Map<String, Object>> page(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Role> rolePage = roleService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", rolePage.getRecords());
        response.put("total", rolePage.getTotal());
        return R.success(response);
    }

    @RequirePermission("role:add")
    @PostMapping
    public R<?> add(@RequestBody Role role) {
        roleService.save(role);
        return R.success();
    }

    @RequirePermission("role:update")
    @PutMapping
    public R<?> update(@RequestBody Role role) {
        roleService.updateById(role);
        return R.success();
    }

    @RequirePermission("role:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return R.success();
    }

    @GetMapping("/{id}")
    public R<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return R.success(role);
    }
}