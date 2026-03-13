package io.github.flexiadmin.framework.service.impl;

import io.github.flexiadmin.framework.entity.UserRole;
import io.github.flexiadmin.framework.mapper.UserRoleMapper;
import io.github.flexiadmin.framework.service.UserRoleService;
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
