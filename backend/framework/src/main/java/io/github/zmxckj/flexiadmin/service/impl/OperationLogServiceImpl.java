package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zmxckj.flexiadmin.entity.OperationLog;
import io.github.zmxckj.flexiadmin.mapper.OperationLogMapper;
import io.github.zmxckj.flexiadmin.service.OperationLogService;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
}