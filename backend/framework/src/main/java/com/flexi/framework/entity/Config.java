package com.flexi.framework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_config")
public class Config {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String configKey;
    private String value;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}