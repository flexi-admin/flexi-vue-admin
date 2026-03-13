package com.flexi.framework.service.impl;

import com.flexi.framework.entity.UserRole;
import com.flexi.framework.mapper.UserRoleMapper;
import com.flexi.framework.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关联表 服务实现类
 * </p>
 *
 * @author flexi
 * @since 2026-03-12
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
