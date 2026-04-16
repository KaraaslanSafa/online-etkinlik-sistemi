package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger / OpenAPI 3.0 yapılandırması
 * API belgelerini http://localhost:8080/swagger-ui.html adresinden görüntüleyebilirsiniz
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Etkinlik Yönetim Sistemi API")
                .version("1.0.0")
                .description("Etkinlikler, katılımcılar ve kayıtları yönetmek için kapsamlı REST API. " +
                    "Bu API ile etkinlik oluşturabilir, katılımcıları yönetebilir ve kayıtları yönetebilirsiniz.")
                .contact(new Contact()
                    .name("Destek Ekibi")
                    .email("support@eventmanagement.com")
                    .url("https://www.eventmanagement.com"))
                .license(new License()
                    .name("MIT Açık Kaynak Lisansı")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Geliştirme Sunucusu"),
                new Server()
                    .url("https://api.eventmanagement.com")
                    .description("Üretim Sunucusu")
            ));
    }
}
