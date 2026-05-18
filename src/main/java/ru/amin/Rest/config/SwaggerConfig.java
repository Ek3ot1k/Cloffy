package ru.amin.Rest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Конфигурация Swagger UI с поддержкой JWT-авторизации
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // Название схемы авторизации
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Cloffy API")
                        .description("Location sharing app — аналог Zenly")
                        .version("1.0.0"))
                // Применяем JWT-авторизацию глобально ко всем эндпоинтам
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
