package io.github.zmxckj.flexiadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.Menu;
import io.github.zmxckj.flexiadmin.entity.User;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
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

    @RequirePermission("menu:list")
    @GetMapping("/list")
    public R<List<Menu>> list() {
        List<Menu> menus = menuService.findAll();
        return R.success(menus);
    }

    @RequirePermission("menu:list")
    @GetMapping("/page")
    public R<Map<String, Object>> page(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Menu> menuPage = menuService.page(new Page<>(page, pageSize));
        Map<String, Object> response = new HashMap<>();
        response.put("list", menuPage.getRecords());
        response.put("total", menuPage.getTotal());
        return R.success(response);
    }

    @RequirePermission("menu:add")
    @PostMapping
    public R<?> add(@RequestBody Menu menu) {
        menuService.save(menu);
        return R.success();
    }

    @RequirePermission("menu:update")
    @PutMapping
    public R<?> update(@RequestBody Menu menu) {
        menuService.updateById(menu);
        return R.success();
    }

    @RequirePermission("menu:delete")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        menuService.removeById(id);
        return R.success();
    }

    @GetMapping("/{id}")
    public R<Menu> getById(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        return R.success(menu);
    }

    @GetMapping("/role/{roleId}")
    public R<List<Menu>> getByRoleId(@PathVariable Long roleId) {
        List<Menu> menus = menuService.findByRoleId(roleId);
        return R.success(menus);
    }

    @GetMapping("/tree")
    public R<List<Menu>> getMenuTree() {
        // 获取当前登录用户
        System.out.println("===== MenuController.getMenuTree() called =====");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        if (authentication == null) {
            System.out.println("Authentication is null, returning all menus");
            return R.success(menuService.getMenuTree());
        }
        System.out.println("Authentication class: " + authentication.getClass());
        System.out.println("Is authenticated: " + authentication.isAuthenticated());
        System.out.println("Authentication name: " + authentication.getName());
        
        if (!authentication.isAuthenticated()) {
            System.out.println("Not authenticated, returning all menus");
            return R.success(menuService.getMenuTree());
        }
        
        String username = authentication.getName();
        System.out.println("Username: " + username);
        User user = userService.findByUsername(username);
        System.out.println("User: " + user);
        if (user == null) {
            System.out.println("User not found, returning all menus");
            return R.success(menuService.getMenuTree());
        }
        
        // 根据用户ID获取菜单树
        System.out.println("Getting menu tree for user: " + user.getUsername());
        List<Menu> menuTree = menuService.getMenuTreeByUserId(user.getId());
        System.out.println("Menu tree size: " + menuTree.size());
        System.out.println("Menu tree: " + menuTree);
        return R.success(menuTree);
    }

    @GetMapping("/tree/{userId}")
    public R<List<Menu>> getMenuTreeByUserId(@PathVariable Long userId) {
        List<Menu> menuTree = menuService.getMenuTreeByUserId(userId);
        return R.success(menuTree);
    }
}