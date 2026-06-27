package com.rwtogether.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();
        // 资源目录前缀必须以 / 结尾，否则会被当作文件名前缀导致匹配失败
        if (!location.endsWith("/")) {
            location += "/";
        }
        // 上传的文件通过 /api/uploads/** 对外提供访问
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations(location);
    }
}
