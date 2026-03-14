package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zmxckj.flexiadmin.entity.Dept;
import io.github.zmxckj.flexiadmin.mapper.DeptMapper;
import io.github.zmxckj.flexiadmin.service.DeptService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public String getDeptNameById(Long deptId) {
        Dept dept = getById(deptId);
        return dept != null ? dept.getName() : "";
    }

    @Override
    public List<Dept> tree() {
        // 获取所有部门
        List<Dept> depts = list();
        
        // 构建树形结构
        return buildTree(depts, 0L);
    }

    private List<Dept> buildTree(List<Dept> depts, Long parentId) {
        List<Dept> treeDepts = new ArrayList<>();
        
        for (Dept dept : depts) {
            if (dept.getParentId().equals(parentId)) {
                // 递归构建子部门
                List<Dept> children = buildTree(depts, dept.getId());
                if (!children.isEmpty()) {
                    dept.setChildren(children);
                }
                treeDepts.add(dept);
            }
        }
        
        return treeDepts;
    }

}
