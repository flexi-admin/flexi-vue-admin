package io.github.flexiadmin.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.flexiadmin.framework.entity.Role;
import io.github.flexiadmin.framework.mapper.RoleMapper;
import io.github.flexiadmin.framework.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public List<Role> findAll() {
        return baseMapper.selectList(null);
    }
}