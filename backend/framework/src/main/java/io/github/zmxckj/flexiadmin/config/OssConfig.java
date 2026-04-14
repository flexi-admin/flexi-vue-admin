package io.github.zmxckj.flexiadmin.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    @Value("${flexi.oss.endpoint:}")
    private String endpoint;

    @Value("${flexi.oss.access-key-id:}")
    private String accessKeyId;

    @Value("${flexi.oss.access-key-secret:}")
    private String accessKeySecret;

    @Value("${flexi.oss.bucket-name:}")
    private String bucketName;

    @Value("${flexi.oss.domain:}")
    private String domain;

    @Bean
    public OSS ossClient() {
        if (isOssConfigured()) {
            return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        }
        return null;
    }

    public boolean isOssConfigured() {
        return endpoint != null && !endpoint.isEmpty() &&
               accessKeyId != null && !accessKeyId.isEmpty() &&
               accessKeySecret != null && !accessKeySecret.isEmpty() &&
               bucketName != null && !bucketName.isEmpty();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getDomain() {
        return domain;
    }
}
