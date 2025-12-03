package com.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * Define el RestTemplate como un Bean para que pueda ser inyectado
     * en el SvcCartItemImp y se use para comunicarse con la API Product.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}