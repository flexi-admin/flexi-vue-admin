package com.flexi.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flexi.framework.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);
}