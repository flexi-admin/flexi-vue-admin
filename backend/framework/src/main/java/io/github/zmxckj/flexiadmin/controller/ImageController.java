package io.github.zmxckj.flexiadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.zmxckj.flexiadmin.entity.Config;
import io.github.zmxckj.flexiadmin.entity.Image;
import io.github.zmxckj.flexiadmin.service.ConfigService;
import io.github.zmxckj.flexiadmin.service.ImageService;
import io.github.zmxckj.flexiadmin.security.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.File;

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
    public ResponseEntity<Image> uploadImage(@RequestParam("file") MultipartFile file) {
        if (!isImageModuleEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        try {
            Image image = imageService.uploadImage(file);
            return ResponseEntity.ok(image);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/list")
    @RequirePermission("image:list")
    public ResponseEntity<IPage<Image>> getImageList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        if (!isImageModuleEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        Page<Image> pageInfo = new Page<>(page, size);
        IPage<Image> result = imageService.page(pageInfo);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("image:delete")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        if (!isImageModuleEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
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
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
