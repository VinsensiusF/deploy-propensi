package com.unimate.unimate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Adjust the mapping to match your API endpoints
            .allowedOrigins("http://localhost:5173") // Replace this with your frontend origin
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*");

        registry.addMapping("/api/**") // Adjust the mapping to match your API endpoints
            .allowedOrigins("https://unimate-frontend.vercel.app") // Replace this with your frontend origin
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*");
        
    }
}
