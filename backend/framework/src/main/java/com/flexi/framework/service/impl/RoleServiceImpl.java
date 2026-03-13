package com.flexi.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.framework.entity.Role;
import com.flexi.framework.mapper.RoleMapper;
import com.flexi.framework.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public List<Role> findAll() {
        return baseMapper.selectList(null);
    }
}