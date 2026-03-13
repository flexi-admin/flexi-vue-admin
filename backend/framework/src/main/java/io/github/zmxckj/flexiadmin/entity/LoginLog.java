package io.github.zmxckj.flexiadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_login_log")
public class LoginLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String ip;
    private Boolean status;
    private String message;
    private LocalDateTime createTime;
}