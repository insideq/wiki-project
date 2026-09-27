package com.example.wiki.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("front")
@Configuration
public class RestCacheConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/assets/*",
                "/images/*")
                .addResourceLocations(
                        "classpath:/static/assets/",
                        "classpath:/static/images/")
                .setCachePeriod(3600 * 24);
    }
}
