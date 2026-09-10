package com.municipality.waste.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Serves files under app.upload.dir at /uploads/**. Without this, uploaded
 * photos are stored on disk but were never actually reachable by URL — the
 * frontend's <img src="/uploads/xxx.jpg"> requests would 404.
 *
 * Message/document attachments deliberately do NOT rely on this public
 * mapping — they're sensitive inter-business communications, so they're
 * served through an authenticated, business-scoped controller endpoint
 * instead (see MessageController).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = new File(uploadDir).getAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + File.separator);
    }
}
