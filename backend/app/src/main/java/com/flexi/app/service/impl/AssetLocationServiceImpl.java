package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.AssetLocation;
import com.flexi.app.entity.AssetLocationTree;
import com.flexi.app.mapper.AssetLocationMapper;
import com.flexi.app.service.AssetLocationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetLocationServiceImpl extends ServiceImpl<AssetLocationMapper, AssetLocation> implements AssetLocationService {
    @Override
    public List<AssetLocationTree> tree() {
        List<AssetLocation> allLocations = list();
        return buildTree(allLocations, 0L);
    }

    private List<AssetLocationTree> buildTree(List<AssetLocation> allLocations, Long parentId) {
        return allLocations.stream()
                .filter(location -> parentId.equals(location.getParentId()))
                .map(this::convertToTree)
                .peek(tree -> tree.setChildren(buildTree(allLocations, tree.getId())))
                .collect(Collectors.toList());
    }

    private AssetLocationTree convertToTree(AssetLocation location) {
        AssetLocationTree tree = new AssetLocationTree();
        tree.setId(location.getId());
        tree.setName(location.getName());
        tree.setParentId(location.getParentId());
        tree.setPath(location.getPath());
        tree.setLevel(location.getLevel());
        tree.setRemark(location.getRemark());
        tree.setStatus(location.getStatus());
        tree.setCreateTime(location.getCreateTime());
        tree.setUpdateTime(location.getUpdateTime());
        return tree;
    }
}