package io.github.flexiadmin.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.flexiadmin.framework.entity.RoleMenu;
import io.github.flexiadmin.framework.mapper.RoleMenuMapper;
import io.github.flexiadmin.framework.service.RoleMenuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        List<RoleMenu> roleMenus = baseMapper.selectList(new QueryWrapper<RoleMenu>().eq("role_id", roleId));
        return roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    public void removeByRoleId(Long roleId) {
        baseMapper.delete(new QueryWrapper<RoleMenu>().eq("role_id", roleId));
    }

    @Override
    public void saveRoleMenu(Long roleId, Long menuId) {
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setRoleId(roleId);
        roleMenu.setMenuId(menuId);
        baseMapper.insert(roleMenu);
    }
}