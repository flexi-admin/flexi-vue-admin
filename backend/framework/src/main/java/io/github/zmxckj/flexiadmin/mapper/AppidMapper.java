package io.github.zmxckj.flexiadmin.mapper;

import io.github.zmxckj.flexiadmin.entity.Appid;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface AppidMapper extends BaseMapper<Appid> {
    Appid selectByAppId(String appId);
}
