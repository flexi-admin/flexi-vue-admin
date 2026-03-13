package io.github.flexiadmin.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.flexiadmin.framework.entity.Config;
import io.github.flexiadmin.framework.mapper.ConfigMapper;
import io.github.flexiadmin.framework.service.ConfigService;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {
}