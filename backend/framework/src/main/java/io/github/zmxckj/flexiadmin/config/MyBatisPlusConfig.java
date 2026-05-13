package io.github.zmxckj.flexiadmin.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {

    @Autowired
    private DataPermissionHandlerImpl dataPermissionHandler;

    @Autowired
    private TenantHandlerImpl tenantHandler;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
        tenantLineInnerInterceptor.setTenantLineHandler(tenantHandler);
        interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        
        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor();
        dataPermissionInterceptor.setDataPermissionHandler(dataPermissionHandler);
        interceptor.addInnerInterceptor(dataPermissionInterceptor);
        
        return interceptor;
    }
}