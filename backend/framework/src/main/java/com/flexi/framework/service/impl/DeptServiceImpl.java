package com.flexi.framework.service.impl;

import com.flexi.framework.entity.Dept;
import com.flexi.framework.mapper.DeptMapper;
import com.flexi.framework.service.DeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author flexi
 * @since 2026-03-12
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

}
