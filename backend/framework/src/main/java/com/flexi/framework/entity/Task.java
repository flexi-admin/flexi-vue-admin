package com.flexi.framework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_task")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String cronExpression;
    private String className;
    private Boolean status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}