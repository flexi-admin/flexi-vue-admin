package io.github.flexiadmin.framework.controller;

import io.github.flexiadmin.framework.entity.Menu;
import io.github.flexiadmin.framework.service.MenuService;
import io.github.flexiadmin.framework.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role-permission")
public class RoleMenuController {

    @Autowired
    private RoleMenuService roleMenuService;
    
    @Autowired
    private MenuService menuService;

    // 获取角色的菜单权限
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        List<Long> menuIds = roleMenuService.getMenuIdsByRoleId(roleId);
        return ResponseEntity.ok(menuIds);
    }

    // 保存角色的菜单权限
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveRolePermissions(@RequestBody Map<String, Object> request) {
        Long roleId = Long.valueOf(request.get("roleId").toString());
        List<Long> permissionIds = (List<Long>) request.get("permissionIds");
        
        // 先删除原有的权限关联
        roleMenuService.removeByRoleId(roleId);
        
        // 添加新的权限关联
        for (Long menuId : permissionIds) {
            roleMenuService.saveRoleMenu(roleId, menuId);
        }
        
        return ResponseEntity.ok(Map.of("code", 200, "message", "权限保存成功"));
    }

    // 获取菜单权限树
    @GetMapping("/tree")
    public ResponseEntity<List<Menu>> getPermissionTree() {
        List<Menu> menuTree = menuService.getMenuTree();
        return ResponseEntity.ok(menuTree);
    }
}
