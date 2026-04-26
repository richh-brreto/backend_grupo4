package school.sptech.back_end_PI.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API Agenda de Aulas")
                        .version("1.0")
                        .description("Documentação do sistema com autenticação JWT"))
                // 1. Adiciona a exigência de segurança global (faz aparecer os cadeados nos métodos)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // 2. Define o componente de segurança (faz aparecer o botão Authorize no topo)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}