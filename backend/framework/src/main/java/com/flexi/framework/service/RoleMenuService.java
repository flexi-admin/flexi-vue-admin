package com.flexi.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.framework.entity.RoleMenu;

import java.util.List;

public interface RoleMenuService extends IService<RoleMenu> {
    List<Long> getMenuIdsByRoleId(Long roleId);
    void removeByRoleId(Long roleId);
    void saveRoleMenu(Long roleId, Long menuId);
}