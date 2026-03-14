package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zmxckj.flexiadmin.entity.User;
import io.github.zmxckj.flexiadmin.mapper.UserMapper;
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
        return user != null ? user.getUsername() : "";
    }
}