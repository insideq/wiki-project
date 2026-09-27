package com.example.wiki.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Profile("nopass")
@Configuration
public class SecurityDevConfiguration {

    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity httpSecurity,
            CorsConfigurationSource corsConfigurationSource) throws Exception {
        httpSecurity.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource));

        httpSecurity.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());

        return httpSecurity.build();
    }
}
