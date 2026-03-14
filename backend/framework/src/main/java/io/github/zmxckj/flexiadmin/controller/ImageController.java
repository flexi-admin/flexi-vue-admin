package io.github.zmxckj.flexiadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.Config;
import io.github.zmxckj.flexiadmin.entity.Image;
import io.github.zmxckj.flexiadmin.common.R;
import io.github.zmxckj.flexiadmin.service.ConfigService;
import io.github.zmxckj.flexiadmin.service.ImageService;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageService imageService;
    
    @Autowired
    private ConfigService configService;

    private boolean isImageModuleEnabled() {
        Config config = configService.getOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Config>()
                .eq("config_key", "system.enabled_modules"));
        if (config != null) {
            String enabledModules = config.getValue();
            return enabledModules.contains("image");
        }
        return false;
    }

    @PostMapping("/upload")
    @RequirePermission("image:upload")
    public R<Image> uploadImage(@RequestParam("file") MultipartFile file) {
        if (!isImageModuleEnabled()) {
            return R.error(503, "图片模块未启用");
        }
        try {
            Image image = imageService.uploadImage(file);
            return R.success(image);
        } catch (IOException e) {
            return R.error(500, "上传失败");
        }
    }

    @GetMapping("/list")
    @RequirePermission("image:list")
    public R<IPage<Image>> getImageList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        if (!isImageModuleEnabled()) {
            return R.error(503, "图片模块未启用");
        }
        Page<Image> pageInfo = new Page<>(page, size);
        IPage<Image> result = imageService.page(pageInfo);
        // 处理图片路径，转换为可访问的URL
        result.getRecords().forEach(image -> {
            // 这里需要根据实际部署情况修改，这里假设图片访问路径为 /api/image/view/{id}
            image.setFilePath("/api/image/view/" + image.getId());
        });
        return R.success(result);
    }
    
    @GetMapping("/view/{id}")
    public ResponseEntity<byte[]> viewImage(@PathVariable Long id) {
        if (!isImageModuleEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        Image image = imageService.getById(id);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            File file = new File(image.getFilePath());
            byte[] bytes = Files.readAllBytes(file.toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.getFileType()))
                    .body(bytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("image:delete")
    public R<?> deleteImage(@PathVariable Long id) {
        if (!isImageModuleEnabled()) {
            return R.error(503, "图片模块未启用");
        }
        Image image = imageService.getById(id);
        if (image != null) {
            // 删除文件
            File file = new File(image.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            // 删除数据库记录
            imageService.removeById(id);
            return R.success();
        }
        return R.error(404, "图片不存在");
    }
}
