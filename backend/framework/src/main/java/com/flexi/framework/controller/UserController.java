package com.flexi.framework.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flexi.framework.entity.User;
import com.flexi.framework.entity.UserRole;
import com.flexi.framework.entity.UserDept;
import com.flexi.framework.service.UserService;
import com.flexi.framework.service.UserRoleService;
import com.flexi.framework.service.UserDeptService;
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
    public ResponseEntity<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<User> userPage = userService.page(new Page<>(page, pageSize));
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
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "添加成功");
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> update(@RequestBody User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateById(user);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        userService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assignRole")
    public ResponseEntity<Map<String, Object>> assignRole(@RequestParam Long userId, @RequestParam List<Long> roleIds) {
        // 先删除用户之前的角色关联
        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", userId));
        // 新增用户与角色的关联
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleService.save(userRole);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "分配角色成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的角色列表
     */
    @GetMapping("/roles/{userId}")
    public ResponseEntity<List<Long>> getUserRoles(@PathVariable Long userId) {
        List<UserRole> userRoles = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", userId));
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(roleIds);
    }

    /**
     * 为用户分配部门
     */
    @PostMapping("/assignDept")
    public ResponseEntity<Map<String, Object>> assignDept(@RequestParam Long userId, @RequestParam List<Long> deptIds) {
        // 先删除用户之前的部门关联
        userDeptService.remove(new QueryWrapper<UserDept>().eq("user_id", userId));
        // 新增用户与部门的关联
        for (Long deptId : deptIds) {
            UserDept userDept = new UserDept();
            userDept.setUserId(userId);
            userDept.setDeptId(deptId);
            userDeptService.save(userDept);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "分配部门成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的部门列表
     */
    @GetMapping("/depts/{userId}")
    public ResponseEntity<List<Long>> getUserDepts(@PathVariable Long userId) {
        List<UserDept> userDepts = userDeptService.list(new QueryWrapper<UserDept>().eq("user_id", userId));
        List<Long> deptIds = userDepts.stream().map(UserDept::getDeptId).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(deptIds);
    }
}