package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zmxckj.flexiadmin.entity.LoginLog;
import io.github.zmxckj.flexiadmin.mapper.LoginLogMapper;
import io.github.zmxckj.flexiadmin.service.LoginLogService;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
}