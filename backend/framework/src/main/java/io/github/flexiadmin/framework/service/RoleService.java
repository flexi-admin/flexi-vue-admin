package io.github.flexiadmin.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.flexiadmin.framework.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {
    List<Role> findAll();
}