package com.example.wiki.security.rest;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.wiki.api.user.AuthController;
import com.example.wiki.configuration.Constants;
import com.example.wiki.entity.UserRole;
import com.example.wiki.error.RestErrorController;

@Profile("front && !nopass")
@Configuration
@EnableMethodSecurity
public class SecurityRestConfiguration {

    public static final List<String> WHITELISTED_URLS = List.of(
            "/index.html",
            "/icon.svg",
            "/app/**",
            "/assets/*",
            "/images/*",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**");

    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity httpSecurity,
            JwtRequestFilter jwtRequestFilter,
            CorsConfigurationSource corsConfigurationSource,
            RestErrorController restErrorController) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource));

        httpSecurity.formLogin(AbstractHttpConfigurer::disable);
        httpSecurity.httpBasic(AbstractHttpConfigurer::disable);
        httpSecurity.logout(AbstractHttpConfigurer::disable);

        httpSecurity.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.authorizeHttpRequests(requests -> requests
                .requestMatchers("/").permitAll()
                .requestMatchers(WHITELISTED_URLS.toArray(new String[0])).permitAll());

        httpSecurity.authorizeHttpRequests(requests -> requests
                .requestMatchers("/h2-console/**").hasRole(UserRole.ADMIN.name())
                .requestMatchers(HttpMethod.POST, AuthController.URL + Constants.SIGNUP_URL).anonymous()
                .requestMatchers(HttpMethod.POST, AuthController.URL + Constants.LOGIN_URL).anonymous()
                .requestMatchers(Constants.API_URL + "/**").authenticated()
                .anyRequest().denyAll());

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.exceptionHandling(exHandling -> exHandling.authenticationEntryPoint(restErrorController));

        return httpSecurity.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
