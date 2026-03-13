package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zmxckj.flexiadmin.entity.Role;
import io.github.zmxckj.flexiadmin.mapper.RoleMapper;
import io.github.zmxckj.flexiadmin.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public List<Role> findAll() {
        return baseMapper.selectList(null);
    }
}