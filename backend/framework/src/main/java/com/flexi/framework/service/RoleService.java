package com.flexi.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.framework.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {
    List<Role> findAll();
}