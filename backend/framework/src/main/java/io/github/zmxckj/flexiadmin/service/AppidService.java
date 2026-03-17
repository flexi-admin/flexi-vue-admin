package io.github.zmxckj.flexiadmin.service;

import io.github.zmxckj.flexiadmin.entity.Appid;

public interface AppidService {
    Appid findByAppId(String appId);
}
