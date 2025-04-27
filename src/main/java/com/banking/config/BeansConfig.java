package com.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


import java.util.Arrays;

@Configuration
public class BeansConfig {
    @Bean
    public CorsFilter corsFilter(){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration cconfig = new CorsConfiguration();
        cconfig.setAllowCredentials(true);
        cconfig.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:4300/"));
        cconfig.setAllowedHeaders(Arrays.asList(HttpHeaders.ORIGIN, HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT));
        cconfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        source.registerCorsConfiguration("/**", cconfig);
        return new CorsFilter(source);
    }

}
