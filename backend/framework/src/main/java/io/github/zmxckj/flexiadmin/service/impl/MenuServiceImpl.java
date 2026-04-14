package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zmxckj.flexiadmin.entity.Menu;
import io.github.zmxckj.flexiadmin.entity.User;
import io.github.zmxckj.flexiadmin.mapper.MenuMapper;
import io.github.zmxckj.flexiadmin.service.MenuService;
import io.github.zmxckj.flexiadmin.service.RoleMenuService;
import io.github.zmxckj.flexiadmin.service.UserService;
import io.github.zmxckj.flexiadmin.service.UserRoleService;
import io.github.zmxckj.flexiadmin.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleMenuService roleMenuService;
    
    @Autowired
    private UserRoleService userRoleService;

    @Override
    public List<Menu> findAll() {
        return baseMapper.selectList(null);
    }

    @Override
    public List<Menu> findByRoleId(Long roleId) {
        List<Long> menuIds = roleMenuService.getMenuIdsByRoleId(roleId);
        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }
        return baseMapper.selectList(new QueryWrapper<Menu>().in("id", menuIds));
    }

    @Override
    public List<Menu> getMenuTree() {
        // 获取所有状态为正常且显示在菜单的菜单和操作，并按order_num排序
        List<Menu> allMenus = baseMapper.selectList(new QueryWrapper<Menu>()
                .eq("status", true)
                .orderByAsc("order_num"));
        
        // 构建菜单树
        return buildMenuTree(allMenus);
    }

    @Override
    public List<Menu> getMenuTreeByUserId(Long userId) {
        // 获取用户信息
        System.out.println("Getting menu tree for user id: " + userId);
        User user = userService.getById(userId);
        System.out.println("User: " + user);
        if (user == null) {
            System.out.println("User not found");
            return new ArrayList<>();
        }
        
        // 获取用户的所有角色ID
        List<UserRole> userRoles = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", userId));
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        System.out.println("User role ids: " + roleIds);
        if (roleIds.isEmpty()) {
            System.out.println("No role ids found");
            return new ArrayList<>();
        }
        
        // 获取所有角色对应的菜单ID
        List<Long> menuIds = new ArrayList<>();
        for (Long roleId : roleIds) {
            menuIds.addAll(roleMenuService.getMenuIdsByRoleId(roleId));
        }
        // 去重
        menuIds = menuIds.stream().distinct().collect(Collectors.toList());
        System.out.println("Menu ids: " + menuIds);
        if (menuIds.isEmpty()) {
            System.out.println("No menu ids found");
            return new ArrayList<>();
        }
        
        // 获取用户有权限的菜单类型且状态为正常且显示在菜单的菜单，并按order_num排序
        List<Menu> userMenus = baseMapper.selectList(new QueryWrapper<Menu>()
                .in("id", menuIds)
                .eq("type", "menu")
                .eq("status", true)
                .orderByAsc("order_num"));
        System.out.println("User menus size: " + userMenus.size());
        
        // 构建菜单树
        List<Menu> menuTree = buildMenuTree(userMenus);
        System.out.println("Menu tree size: " + menuTree.size());
        return menuTree;
    }
    
    @Override
    public List<Menu> getOperationsByUserId(Long userId) {
        // 获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        
        // 获取用户的所有角色ID
        List<UserRole> userRoles = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", userId));
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有角色对应的菜单ID
        List<Long> menuIds = new ArrayList<>();
        for (Long roleId : roleIds) {
            menuIds.addAll(roleMenuService.getMenuIdsByRoleId(roleId));
        }
        // 去重
        menuIds = menuIds.stream().distinct().collect(Collectors.toList());
        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取用户有权限的操作类型且状态为正常的菜单
        return baseMapper.selectList(new QueryWrapper<Menu>()
                .in("id", menuIds)
                .eq("type", "operation")
                .eq("status", true));
    }

    private List<Menu> buildMenuTree(List<Menu> menus) {
        // 按父ID分组
        Map<Long, List<Menu>> menuMap = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));
        
        // 构建根菜单
        List<Menu> rootMenus = menuMap.getOrDefault(0L, new ArrayList<>());
        
        // 递归构建子菜单
        for (Menu rootMenu : rootMenus) {
            buildChildren(rootMenu, menuMap);
        }
        
        return rootMenus;
    }

    private void buildChildren(Menu parent, Map<Long, List<Menu>> menuMap) {
        List<Menu> children = menuMap.getOrDefault(parent.getId(), new ArrayList<>());
        if (!children.isEmpty()) {
            // 对子菜单按order_num排序
            children.sort((a, b) -> Integer.compare(a.getOrderNum(), b.getOrderNum()));
            parent.setChildren(children);
            for (Menu child : children) {
                buildChildren(child, menuMap);
            }
        }
    }
}