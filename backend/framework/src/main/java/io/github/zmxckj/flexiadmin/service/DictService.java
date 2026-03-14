package io.github.zmxckj.flexiadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.zmxckj.flexiadmin.entity.Dict;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface DictService extends IService<Dict> {
    Page<Dict> page(Page<Dict> page, String type);
    String getDictLabel(String type, String value);
    List<Dict> listByType(String type);
}