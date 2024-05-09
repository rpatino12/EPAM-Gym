package com.rpatino12.epam.gym.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    // To see the OpenAPI (Swagger) documentation (go to http://localhost:8080/doc/swagger-ui.html)
    @Bean
    public OpenAPI apiDocConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("Application Rest API")
                        .description("EPAM Gym Application API")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Ricardo Patino")
                                .url("https://rpatino12.github.io/")
                                .email("rpatino12@outlook.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Apache 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.html"));
    }
}
