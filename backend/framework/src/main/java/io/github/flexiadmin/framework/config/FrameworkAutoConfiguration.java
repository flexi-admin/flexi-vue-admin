package io.github.flexiadmin.framework.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "io.github.flexiadmin.framework")
@MapperScan("io.github.flexiadmin.framework.mapper")
public class FrameworkAutoConfiguration {
}
