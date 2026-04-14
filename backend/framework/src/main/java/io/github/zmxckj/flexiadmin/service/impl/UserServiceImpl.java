package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zmxckj.flexiadmin.entity.User;
import io.github.zmxckj.flexiadmin.mapper.UserMapper;
import io.github.zmxckj.flexiadmin.security.SecurityUtils;
import io.github.zmxckj.flexiadmin.service.UserService;
import io.github.zmxckj.flexiadmin.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public User findByUsername(String username) {
        return baseMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }

    @Override
    public User findByUsernameAndTenantId(String username, Long tenantId) {
        return baseMapper.selectOne(new QueryWrapper<User>().eq("username", username).eq("tenant_id", tenantId));
    }

    @Override
    public boolean save(User user) {
        boolean result = super.save(user);
        if (result) {
            // 清除相关缓存
            redisUtils.removeUserInfo(user.getUsername());
        }
        return result;
    }

    @Override
    public boolean updateById(User user) {
        boolean result = super.updateById(user);
        if (result) {
            // 清除相关缓存
            redisUtils.removeUserInfo(user.getUsername());
        }
        return result;
    }

    @Override
    public boolean removeById(Serializable id) {
        User user = getById(id);
        boolean result = super.removeById(id);
        if (result && user != null) {
            // 清除相关缓存
            redisUtils.removeUserInfo(user.getUsername());
        }
        return result;
    }

    @Override
    public String getUserNameById(Long userId) {
        User user = getById(userId);
        if (user != null) {
            return user.getNickname() != null && !user.getNickname().isEmpty() ? user.getNickname() : user.getUsername();
        }
        return "";
    }

    @Override
    public Page<User> page(Page<User> page, String keyword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("username", keyword);
        }
        // 添加租户ID查询条件
        Long tenantId = SecurityUtils.getCurrentTenantId();
        if (tenantId != null) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        return super.page(page, queryWrapper);
    }
}