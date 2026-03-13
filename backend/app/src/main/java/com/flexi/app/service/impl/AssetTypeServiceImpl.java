package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.AssetType;
import com.flexi.app.entity.AssetTypeTree;
import com.flexi.app.mapper.AssetTypeMapper;
import com.flexi.app.service.AssetTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetTypeServiceImpl extends ServiceImpl<AssetTypeMapper, AssetType> implements AssetTypeService {
    @Override
    public List<AssetTypeTree> tree() {
        List<AssetType> allTypes = list();
        return buildTree(allTypes, 0L);
    }

    private List<AssetTypeTree> buildTree(List<AssetType> allTypes, Long parentId) {
        return allTypes.stream()
                .filter(type -> parentId.equals(type.getParentId()))
                .map(this::convertToTree)
                .peek(tree -> tree.setChildren(buildTree(allTypes, tree.getId())))
                .collect(Collectors.toList());
    }

    private AssetTypeTree convertToTree(AssetType type) {
        AssetTypeTree tree = new AssetTypeTree();
        tree.setId(type.getId());
        tree.setName(type.getName());
        tree.setParentId(type.getParentId());
        tree.setPath(type.getPath());
        tree.setLevel(type.getLevel());
        tree.setRemark(type.getRemark());
        tree.setStatus(type.getStatus());
        tree.setCreateTime(type.getCreateTime());
        tree.setUpdateTime(type.getUpdateTime());
        return tree;
    }
}