package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.Dict;
import io.github.zmxckj.flexiadmin.mapper.DictMapper;
import io.github.zmxckj.flexiadmin.service.DictService;
import org.springframework.stereotype.Service;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Override
    public Page<Dict> page(Page<Dict> page, String type) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            queryWrapper.like("type", type);
        }
        return super.page(page, queryWrapper);
    }
}