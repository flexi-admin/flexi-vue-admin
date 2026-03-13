package io.github.flexiadmin.framework.service.impl;

import io.github.flexiadmin.framework.entity.UserDept;
import io.github.flexiadmin.framework.mapper.UserDeptMapper;
import io.github.flexiadmin.framework.service.UserDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户部门关联表 服务实现类
 * </p>
 *
 * @author flexi
 * @since 2026-03-12
 */
@Service
public class UserDeptServiceImpl extends ServiceImpl<UserDeptMapper, UserDept> implements UserDeptService {

}
