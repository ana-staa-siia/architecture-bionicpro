package ru.bionicpro.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
//        .authorizeHttpRequests(auth -> auth
//            .requestMatchers("/api/reports/**").authenticated()
//            .anyRequest().permitAll()
//        )
//        .oauth2ResourceServer(oauth2 -> oauth2
//            .jwt(jwt -> {
//              System.out.println("JWT config is loaded!");
//            })
//        );
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll());
    return http.build();
  }
}