package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.Dict;
import io.github.zmxckj.flexiadmin.mapper.DictMapper;
import io.github.zmxckj.flexiadmin.service.DictService;
import org.springframework.stereotype.Service;
import java.util.List;

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

    @Override
    public String getDictLabel(String type, String value) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        queryWrapper.eq("code", value);
        Dict dict = getOne(queryWrapper);
        return dict != null ? dict.getValue() : value;
    }

    @Override
    public List<Dict> listByType(String type) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("type", type);
        }
        return list(queryWrapper);
    }
}