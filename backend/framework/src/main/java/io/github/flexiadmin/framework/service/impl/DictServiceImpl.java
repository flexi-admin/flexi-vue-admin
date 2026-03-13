package io.github.flexiadmin.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.flexiadmin.framework.entity.Dict;
import io.github.flexiadmin.framework.mapper.DictMapper;
import io.github.flexiadmin.framework.service.DictService;
import org.springframework.stereotype.Service;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
}