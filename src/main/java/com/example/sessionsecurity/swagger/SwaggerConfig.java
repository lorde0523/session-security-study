package com.example.sessionsecurity.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Session Security API")
                        .version("v1")
                        .description("SessionVo based authorization sample"))
                .components(new Components()
                        .addSecuritySchemes("X-USER-ID", headerScheme("X-USER-ID"))
                        .addSecuritySchemes("X-USER-NAME", headerScheme("X-USER-NAME"))
                        .addSecuritySchemes("X-UUID", headerScheme("X-UUID"))
                        .addSecuritySchemes("X-CLIENT", headerScheme("X-CLIENT"))
                        .addSecuritySchemes("X-ROLES", headerScheme("X-ROLES")))
                .security(List.of(new SecurityRequirement()
                        .addList("X-USER-ID")
                        .addList("X-USER-NAME")
                        .addList("X-UUID")
                        .addList("X-CLIENT")
                        .addList("X-ROLES")));
    }

    private SecurityScheme headerScheme(String name) {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(name);
    }
}
