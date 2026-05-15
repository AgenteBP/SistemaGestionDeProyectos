package com.ChallengeSpringBoot.SistemaGestionDeProyectos.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Sistema de Gestión de Proyectos API")
                                                .version("1.0.0")
                                                .description(
                                                                "API REST para la gestión de proyectos, tareas, pasos, usuarios y comentarios. "));
        }

}
