package com.flexi.framework.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.flexi.framework")
@MapperScan("com.flexi.framework.mapper")
public class FrameworkAutoConfiguration {
}
