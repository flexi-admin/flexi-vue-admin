package io.github.zmxckj.flexiadmin.controller;

import io.github.zmxckj.flexiadmin.entity.Dept;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import io.github.zmxckj.flexiadmin.service.DeptService;
import io.github.zmxckj.flexiadmin.service.UserDeptService;
import io.github.zmxckj.flexiadmin.entity.UserDept;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * <p>
 * 部门管理控制器
 * </p>
 *
 * @author flexi
 * @since 2026-03-12
 */
@RestController
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @Autowired
    private UserDeptService userDeptService;

    /**
     * 部门列表
     */
    @RequirePermission("dept:list")
    @GetMapping("/list")
    public R<List<Dept>> list() {
        return R.success(deptService.list());
    }

    /**
     * 部门分页列表
     */
    @RequirePermission("dept:list")
    @GetMapping("/page")
    public R<Page<Dept>> page(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return R.success(deptService.page(new Page<>(page, size)));
    }

    /**
     * 新增部门
     */
    @RequirePermission("dept:add")
    @PostMapping
    public R<?> save(@RequestBody Dept dept) {
        deptService.save(dept);
        return R.success();
    }

    /**
     * 修改部门
     */
    @RequirePermission("dept:update")
    @PutMapping
    public R<?> update(@RequestBody Dept dept) {
        deptService.updateById(dept);
        return R.success();
    }

    /**
     * 删除部门
     */
    @RequirePermission("dept:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        deptService.removeById(id);
        return R.success();
    }

    /**
     * 部门树形结构
     */
    @GetMapping("/tree")
    public R<List<Dept>> tree() {
        return R.success(deptService.tree());
    }

}
