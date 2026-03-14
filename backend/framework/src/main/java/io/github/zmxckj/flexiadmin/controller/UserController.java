package io.github.zmxckj.flexiadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.User;
import io.github.zmxckj.flexiadmin.entity.UserRole;
import io.github.zmxckj.flexiadmin.entity.UserDept;
import io.github.zmxckj.flexiadmin.dto.AssignRoleDTO;
import io.github.zmxckj.flexiadmin.dto.AssignDeptDTO;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.service.UserService;
import io.github.zmxckj.flexiadmin.service.UserRoleService;
import io.github.zmxckj.flexiadmin.service.UserDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserDeptService userDeptService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    public R<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(required = false) String keyword) {
        Page<User> userPage = userService.page(new Page<>(page, pageSize), keyword);
        List<User> users = userPage.getRecords();
        
        // 为每个用户添加角色列表和部门列表
        List<Map<String, Object>> userListWithDetails = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("status", user.getStatus());
            userMap.put("createTime", user.getCreateTime());
            userMap.put("updateTime", user.getUpdateTime());
            
            // 获取用户的角色列表
            List<UserRole> userRoles = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", user.getId()));
            List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(java.util.stream.Collectors.toList());
            userMap.put("roleIds", roleIds);
            
            // 获取用户的部门列表
            List<UserDept> userDepts = userDeptService.list(new QueryWrapper<UserDept>().eq("user_id", user.getId()));
            List<Long> deptIds = userDepts.stream().map(UserDept::getDeptId).collect(java.util.stream.Collectors.toList());
            userMap.put("deptIds", deptIds);
            
            userListWithDetails.add(userMap);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("list", userListWithDetails);
        response.put("total", userPage.getTotal());
        return R.success(response);
    }

    @PostMapping
    public R<User> add(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return R.success(user);
    }

    @PutMapping
    public R<?> update(@RequestBody User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateById(user);
        return R.success();
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        userService.removeById(id);
        return R.success();
    }

    @GetMapping("/{id}")
    public R<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return R.success(user);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assignRole")
    public R<?> assignRole(@RequestBody AssignRoleDTO assignRoleDTO) {
        Long userId = assignRoleDTO.getUserId();
        List<Long> roleIds = assignRoleDTO.getRoleIds();
        // 先删除用户之前的角色关联
        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", userId));
        // 新增用户与角色的关联
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleService.save(userRole);
        }
        return R.success();
    }

    /**
     * 获取用户的角色列表
     */
    @GetMapping("/roles/{userId}")
    public R<List<Long>> getUserRoles(@PathVariable Long userId) {
        List<UserRole> userRoles = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", userId));
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(java.util.stream.Collectors.toList());
        return R.success(roleIds);
    }

    /**
     * 为用户分配部门
     */
    @PostMapping("/assignDept")
    public R<?> assignDept(@RequestBody AssignDeptDTO assignDeptDTO) {
        Long userId = assignDeptDTO.getUserId();
        List<Long> deptIds = assignDeptDTO.getDeptIds();
        // 先删除用户之前的部门关联
        userDeptService.remove(new QueryWrapper<UserDept>().eq("user_id", userId));
        // 新增用户与部门的关联
        for (Long deptId : deptIds) {
            UserDept userDept = new UserDept();
            userDept.setUserId(userId);
            userDept.setDeptId(deptId);
            userDeptService.save(userDept);
        }
        return R.success();
    }

    /**
     * 获取用户的部门列表
     */
    @GetMapping("/depts/{userId}")
    public R<List<Long>> getUserDepts(@PathVariable Long userId) {
        List<UserDept> userDepts = userDeptService.list(new QueryWrapper<UserDept>().eq("user_id", userId));
        List<Long> deptIds = userDepts.stream().map(UserDept::getDeptId).collect(java.util.stream.Collectors.toList());
        return R.success(deptIds);
    }
}