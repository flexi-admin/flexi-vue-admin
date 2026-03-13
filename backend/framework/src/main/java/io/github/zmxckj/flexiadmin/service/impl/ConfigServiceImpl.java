package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zmxckj.flexiadmin.entity.Config;
import io.github.zmxckj.flexiadmin.mapper.ConfigMapper;
import io.github.zmxckj.flexiadmin.service.ConfigService;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {
}