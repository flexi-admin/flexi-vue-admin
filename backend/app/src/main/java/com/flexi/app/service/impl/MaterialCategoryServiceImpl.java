package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.MaterialCategory;
import com.flexi.app.entity.MaterialCategoryTree;
import com.flexi.app.mapper.MaterialCategoryMapper;
import com.flexi.app.service.MaterialCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialCategoryServiceImpl extends ServiceImpl<MaterialCategoryMapper, MaterialCategory> implements MaterialCategoryService {
    @Override
    public List<MaterialCategoryTree> tree() {
        List<MaterialCategory> allCategories = list();
        return buildTree(allCategories, 0L);
    }

    private List<MaterialCategoryTree> buildTree(List<MaterialCategory> allCategories, Long parentId) {
        return allCategories.stream()
                .filter(category -> parentId.equals(category.getParentId()))
                .map(this::convertToTree)
                .peek(tree -> tree.setChildren(buildTree(allCategories, tree.getId())))
                .collect(Collectors.toList());
    }

    private MaterialCategoryTree convertToTree(MaterialCategory category) {
        MaterialCategoryTree tree = new MaterialCategoryTree();
        tree.setId(category.getId());
        tree.setName(category.getName());
        tree.setParentId(category.getParentId());
        tree.setPath(category.getPath());
        tree.setLevel(category.getLevel());
        tree.setRemark(category.getRemark());
        tree.setStatus(category.getStatus());
        tree.setCreateTime(category.getCreateTime());
        tree.setUpdateTime(category.getUpdateTime());
        return tree;
    }
}
