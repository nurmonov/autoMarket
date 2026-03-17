package org.example.automarket.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.reflections.Reflections;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AutoMarket API")
                        .version("1.0")
                        .description("Avtomobil savdosi platformasi API"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }


    @Bean
    public OpenApiCustomizer enumCustomizer() {
        return openApi -> {

            Reflections reflections =
                    new Reflections("org.example.automarket.entity.enums");

            Set<Class<? extends Enum>> enums =
                    reflections.getSubTypesOf(Enum.class);

            for (Class<? extends Enum> enumClass : enums) {

                StringSchema schema = new StringSchema();

                Object[] constants = enumClass.getEnumConstants();
                for (Object constant : constants) {
                    schema.addEnumItemObject(constant.toString());
                }

                openApi.getComponents()
                        .addSchemas(enumClass.getSimpleName(), schema);
            }
        };
    }
}