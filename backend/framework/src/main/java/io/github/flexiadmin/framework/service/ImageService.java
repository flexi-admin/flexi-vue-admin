package io.github.flexiadmin.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.flexiadmin.framework.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService extends IService<Image> {
    Image uploadImage(MultipartFile file) throws IOException;
}
