package io.github.zmxckj.flexiadmin.config;

import io.github.zmxckj.flexiadmin.excel.service.ExcelExportService;
import io.github.zmxckj.flexiadmin.excel.service.ExcelImportService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "io.github.zmxckj.flexiadmin")
@MapperScan("io.github.zmxckj.flexiadmin.mapper")
public class FrameworkAutoConfiguration {

    @Bean
    public ExcelImportService excelImportService() {
        return new ExcelImportService();
    }

    @Bean
    public ExcelExportService excelExportService() {
        return new ExcelExportService();
    }
}
