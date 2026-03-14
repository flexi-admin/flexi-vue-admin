package io.github.zmxckj.flexiadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.zmxckj.flexiadmin.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface UserService extends IService<User> {
    User findByUsername(String username);
    String getUserNameById(Long userId);
    Page<User> page(Page<User> page, String keyword);
}