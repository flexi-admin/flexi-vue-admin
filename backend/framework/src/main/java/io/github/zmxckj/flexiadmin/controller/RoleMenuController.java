package io.github.zmxckj.flexiadmin.controller;

import io.github.zmxckj.flexiadmin.entity.Menu;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.dto.SaveRolePermissionDTO;
import io.github.zmxckj.flexiadmin.service.MenuService;
import io.github.zmxckj.flexiadmin.service.RoleMenuService;
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
    public R<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        List<Long> menuIds = roleMenuService.getMenuIdsByRoleId(roleId);
        return R.success(menuIds);
    }

    // 保存角色的菜单权限
    @PostMapping
    public R<?> saveRolePermissions(@RequestBody SaveRolePermissionDTO saveRolePermissionDTO) {
        Long roleId = saveRolePermissionDTO.getRoleId();
        List<Long> permissionIds = saveRolePermissionDTO.getPermissionIds();
        
        // 先删除原有的权限关联
        roleMenuService.removeByRoleId(roleId);
        
        // 添加新的权限关联
        for (Long menuId : permissionIds) {
            roleMenuService.saveRoleMenu(roleId, menuId);
        }
        
        return R.success();
    }

    // 获取菜单权限树
    @GetMapping("/tree")
    public R<List<Menu>> getPermissionTree() {
        List<Menu> menuTree = menuService.getMenuTree();
        return R.success(menuTree);
    }
}
