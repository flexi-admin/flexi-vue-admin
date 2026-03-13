package io.github.zmxckj.flexiadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.Menu;
import io.github.zmxckj.flexiadmin.entity.User;
import io.github.zmxckj.flexiadmin.service.MenuService;
import io.github.zmxckj.flexiadmin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<Menu>> list() {
        List<Menu> menus = menuService.findAll();
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> page(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Menu> menuPage = menuService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", menuPage.getRecords());
        response.put("total", menuPage.getTotal());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody Menu menu) {
        menuService.save(menu);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "添加成功");
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> update(@RequestBody Menu menu) {
        menuService.updateById(menu);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        menuService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Menu> getById(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<Menu>> getByRoleId(@PathVariable Long roleId) {
        List<Menu> menus = menuService.findByRoleId(roleId);
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/tree")
    public ResponseEntity<List<Menu>> getMenuTree() {
        // 获取当前登录用户
        System.out.println("===== MenuController.getMenuTree() called =====");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        if (authentication == null) {
            System.out.println("Authentication is null, returning all menus");
            return ResponseEntity.ok(menuService.getMenuTree());
        }
        System.out.println("Authentication class: " + authentication.getClass());
        System.out.println("Is authenticated: " + authentication.isAuthenticated());
        System.out.println("Authentication name: " + authentication.getName());
        
        if (!authentication.isAuthenticated()) {
            System.out.println("Not authenticated, returning all menus");
            return ResponseEntity.ok(menuService.getMenuTree());
        }
        
        String username = authentication.getName();
        System.out.println("Username: " + username);
        User user = userService.findByUsername(username);
        System.out.println("User: " + user);
        if (user == null) {
            System.out.println("User not found, returning all menus");
            return ResponseEntity.ok(menuService.getMenuTree());
        }
        
        // 根据用户ID获取菜单树
        System.out.println("Getting menu tree for user: " + user.getUsername());
        List<Menu> menuTree = menuService.getMenuTreeByUserId(user.getId());
        System.out.println("Menu tree size: " + menuTree.size());
        System.out.println("Menu tree: " + menuTree);
        return ResponseEntity.ok(menuTree);
    }

    @GetMapping("/tree/{userId}")
    public ResponseEntity<List<Menu>> getMenuTreeByUserId(@PathVariable Long userId) {
        List<Menu> menuTree = menuService.getMenuTreeByUserId(userId);
        return ResponseEntity.ok(menuTree);
    }
}