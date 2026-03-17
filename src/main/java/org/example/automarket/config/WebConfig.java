package org.example.automarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {



        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            String userDir = System.getProperty("user.dir");
            String uploadPath = userDir + "/uploads/";  // oxiriga / qo'shdik!

            // Debug uchun konsolga chiqarib qo'ying (JAR ishga tushganda ko'rasiz)
            System.out.println("=== DEBUG: JAR ishga tushgan papka (user.dir): " + userDir);
            System.out.println("=== DEBUG: Uploads yo'li: " + uploadPath);

            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + uploadPath)  // file: + absolute yo'l (Windowsda ishlaydi)
                    .setCachePeriod(3600);
        }

}