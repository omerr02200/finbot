package com.finbot.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinBot API")
                        .description("AI destekli kişisel finans takip sistemi")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Ömer Akıncı")
                                .email("omerr_02200@hotmail.com")
                                .url("https://github.com/omerr02200")));
    }
}
