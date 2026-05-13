package com.flexi.app.entity;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.format.NumberFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("asset")
public class Asset {
    
    @ExcelIgnore
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @ExcelProperty(value = "资产名称", index = 3)
    @ColumnWidth(20)
    private String name;
    
    @ExcelProperty(value = "资产编码", index = 0)
    @ColumnWidth(20)
    private String code;
    
    @ExcelIgnore
    private String typeCode;
    
    @ExcelProperty(value = "自定义类型编码", index = 2)
    @ColumnWidth(15)
    @TableField(exist = false)
    private String customTypeCode;

    @ExcelIgnore
    private Long locationId;

    @TableField(exist = false)
    @ExcelProperty(value = "位置名称", index = 13)
    private String locationName;
    
    @ExcelIgnore
    private String specification;
    
    @ExcelProperty(value = "型号", index = 4)
    @ColumnWidth(15)
    private String model;
    
    @ExcelIgnore
    private String manufacturer;
    
    @ExcelIgnore
    private Long supplierId;
    
    @ExcelIgnore
    private LocalDate purchaseDate;
    
    @ExcelProperty(value = "入库日期", index = 9)
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate warehouseDate;
    
    @ExcelProperty(value = "启用日期", index = 10)
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate enableDate;
    
    @ExcelProperty(value = "价格", index = 7)
    @NumberFormat("#.##")
    @ColumnWidth(15)
    private Double price;
    
    @ExcelIgnore
    private String status;
    
    @ExcelProperty(value = "备注", index = 12)
    @ColumnWidth(30)
    private String remark;
    
    @ExcelIgnore
    private Integer isDeleted;
    
    @ExcelIgnore
    private LocalDateTime createTime;
    
    @ExcelIgnore
    private LocalDateTime updateTime;
    
    @ExcelIgnore
    private String image;
    
    @ExcelIgnore
    private String labelType;
    
    @ExcelIgnore
    private String labelCode;
    
    @ExcelIgnore
    private Long adminUserId;
    
    @ExcelIgnore
    private Long userId;
    
    @ExcelIgnore
    private Long deptId;

    @TableField(exist = false)
    @ExcelProperty(value = "部门名称", index = 11)
    private String deptName;
    
    @ExcelIgnore
    private String sn;
    
    @ExcelIgnore
    private String source;
    
    @ExcelProperty(value = "当前价值", index = 17)
    @NumberFormat("#.##")
    @ColumnWidth(15)
    private Double currentValue;
    
    @ExcelProperty(value = "已计提折旧价值", index = 16)
    @NumberFormat("#.##")
    @ColumnWidth(15)
    private Double accumulatedDepreciationValue;
    
    @ExcelProperty(value = "计量单位", index = 6)
    @ColumnWidth(12)
    private String unit;
    
    @ExcelIgnore
    private Long creatorId;
    
    @ExcelIgnore
    private Long updaterId;
}
