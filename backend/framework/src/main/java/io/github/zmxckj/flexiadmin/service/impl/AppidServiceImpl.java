package io.github.zmxckj.flexiadmin.service.impl;

import io.github.zmxckj.flexiadmin.entity.Appid;
import io.github.zmxckj.flexiadmin.mapper.AppidMapper;
import io.github.zmxckj.flexiadmin.service.AppidService;
import io.github.zmxckj.flexiadmin.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AppidServiceImpl implements AppidService {

    @Autowired
    private AppidMapper appidMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Appid findByAppId(String appId) {
        // 先从Redis缓存中获取
        String key = "appid:" + appId;
        Appid appid = redisUtils.get(key, Appid.class);
        if (appid != null) {
            return appid;
        }

        // 缓存未命中，从数据库中获取
        appid = appidMapper.selectByAppId(appId);
        if (appid != null) {
            // 缓存到Redis，过期时间1小时
            redisUtils.set(key, appid, 3600, TimeUnit.SECONDS);
        }

        return appid;
    }
}
