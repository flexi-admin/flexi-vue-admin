package io.github.zmxckj.flexiadmin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "io.github.zmxckj.flexiadmin")
@MapperScan("io.github.zmxckj.flexiadmin.mapper")
public class FrameworkAutoConfiguration {
}
