package io.github.zmxckj.flexiadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.zmxckj.flexiadmin.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService extends IService<Image> {
    Image uploadImage(MultipartFile file) throws IOException;
    Image uploadImageToOss(MultipartFile file) throws IOException;
}
