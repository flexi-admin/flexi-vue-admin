package io.github.flexiadmin.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.flexiadmin.framework.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);
}