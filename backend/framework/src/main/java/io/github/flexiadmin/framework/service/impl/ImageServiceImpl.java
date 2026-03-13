package io.github.flexiadmin.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.flexiadmin.framework.entity.Image;
import io.github.flexiadmin.framework.mapper.ImageMapper;
import io.github.flexiadmin.framework.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

    @Value("${flexi.upload.dir:${java.io.tmpdir}/flexi-upload/images}")
    private String uploadDir;

    @Value("${flexi.upload.max-size:10485760}") // 10MB
    private long maxFileSize;

    @Override
    public Image uploadImage(MultipartFile file) throws IOException {
        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            throw new IOException("File size exceeds the limit of " + (maxFileSize / 1024 / 1024) + "MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Only image files are allowed");
        }

        // 确保上传目录存在
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.')) : "";
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);

        // 保存文件到本地
        Files.copy(file.getInputStream(), filePath);

        // 创建图片记录
        Image image = new Image();
        image.setFilename(filename);
        image.setOriginalFilename(originalFilename);
        image.setFilePath(filePath.toString());
        image.setFileSize(file.getSize());
        image.setFileType(contentType);
        image.setStatus(true);

        // 保存到数据库
        save(image);

        return image;
    }
}
