package br.com.raizes_do_nordeste.infra.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "Informe o token JWT obtido no endpoint POST /auth/login. Formato: Bearer <token>"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI raizesDoNordesteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Raízes do Nordeste – API")
                        .description("""
                                API Back-End do sistema **Raízes do Nordeste**, uma plataforma de
                                pedidos de comida nordestina com suporte a app, web, totem e atendente.
                                
                                ### Fluxo de autenticação
                                1. Cadastre a unidade (`POST /unidades/cadastro`)
                                2. Cadastre o primeiro gerente (`POST /bootstrap/gerente`)
                                3. Realize o login (`POST /auth/login`) e copie o token JWT
                                4. Clique em **Authorize** e informe o token no formato `Bearer <token>`
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Raízes do Nordeste")
                                .email("suporte@raizesdonordeste.com.br"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

