package io.github.zmxckj.flexiadmin.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {

    @Autowired
    private DataPermissionHandlerImpl dataPermissionHandler;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        
        // 添加数据权限插件
        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor();
        dataPermissionInterceptor.setDataPermissionHandler(dataPermissionHandler);
        interceptor.addInnerInterceptor(dataPermissionInterceptor);
        
        return interceptor;
    }
}

