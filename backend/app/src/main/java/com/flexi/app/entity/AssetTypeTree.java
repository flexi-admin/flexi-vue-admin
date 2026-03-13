package com.flexi.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetTypeTree extends AssetType {
    private List<AssetTypeTree> children;
}