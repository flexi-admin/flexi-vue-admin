package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zmxckj.flexiadmin.entity.Dict;
import io.github.zmxckj.flexiadmin.mapper.DictMapper;
import io.github.zmxckj.flexiadmin.service.DictService;
import org.springframework.stereotype.Service;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
}