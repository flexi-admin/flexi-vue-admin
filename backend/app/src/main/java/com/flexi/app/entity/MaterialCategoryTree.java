package com.flexi.app.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MaterialCategoryTree extends MaterialCategory {
    private List<MaterialCategoryTree> children;
}
