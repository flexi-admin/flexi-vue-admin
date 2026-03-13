package io.github.zmxckj.flexiadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.zmxckj.flexiadmin.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {
    List<Role> findAll();
}