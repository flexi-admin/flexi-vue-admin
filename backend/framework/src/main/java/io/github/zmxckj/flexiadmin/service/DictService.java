package io.github.zmxckj.flexiadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.zmxckj.flexiadmin.entity.Dict;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface DictService extends IService<Dict> {
    Page<Dict> page(Page<Dict> page, String type);
}