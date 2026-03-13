package io.github.zmxckj.flexiadmin.controller;

import io.github.zmxckj.flexiadmin.entity.Dept;
import io.github.zmxckj.flexiadmin.common.R;
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
    @GetMapping("/list")
    public R<List<Dept>> list() {
        return R.success(deptService.list());
    }

    /**
     * 部门分页列表
     */
    @GetMapping("/page")
    public R<Page<Dept>> page(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return R.success(deptService.page(new Page<>(page, size)));
    }

    /**
     * 根据ID获取部门
     */
    @GetMapping("/{id}")
    public R<Dept> getById(@PathVariable Long id) {
        return R.success(deptService.getById(id));
    }

    /**
     * 新增部门
     */
    @PostMapping
    public R<?> save(@RequestBody Dept dept) {
        deptService.save(dept);
        return R.success();
    }

    /**
     * 修改部门
     */
    @PutMapping
    public R<?> update(@RequestBody Dept dept) {
        deptService.updateById(dept);
        return R.success();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        deptService.removeById(id);
        return R.success();
    }


}
