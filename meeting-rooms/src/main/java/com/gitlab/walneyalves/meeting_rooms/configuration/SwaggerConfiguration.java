package com.gitlab.walneyalves.meeting_rooms.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI(@Value("${info.app.name}") String name,
                           @Value("${info.app.description}") String description,
                           @Value("${info.app.version}") String version) {
        val title  = String.format("%s documentation",
                StringUtils.capitalize(name.replaceAll("-", " ")));
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                );
    }
}