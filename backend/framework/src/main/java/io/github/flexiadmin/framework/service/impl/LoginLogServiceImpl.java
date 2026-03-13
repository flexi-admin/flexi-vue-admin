package io.github.flexiadmin.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.flexiadmin.framework.entity.LoginLog;
import io.github.flexiadmin.framework.mapper.LoginLogMapper;
import io.github.flexiadmin.framework.service.LoginLogService;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
}