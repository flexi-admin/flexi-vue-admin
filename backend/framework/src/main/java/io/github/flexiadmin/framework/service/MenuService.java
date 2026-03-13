package io.github.flexiadmin.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.flexiadmin.framework.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    List<Menu> findAll();
    List<Menu> findByRoleId(Long roleId);
    List<Menu> getMenuTree();
    List<Menu> getMenuTreeByUserId(Long userId);
    List<Menu> getOperationsByUserId(Long userId);
}