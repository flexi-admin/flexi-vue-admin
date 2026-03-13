package com.flexi.framework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_dict")
public class Dict {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private String code;
    private String value;
    private Integer orderNum;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}