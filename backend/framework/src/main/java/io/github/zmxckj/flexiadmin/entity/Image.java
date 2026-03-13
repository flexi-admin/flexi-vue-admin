package io.github.zmxckj.flexiadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sys_image")
public class Image {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String filename;
    private String originalFilename;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private Boolean status;
    private Date createTime;
    private Date updateTime;
}
