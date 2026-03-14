package io.github.zmxckj.flexiadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.zmxckj.flexiadmin.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);
    String getUserNameById(Long userId);
}