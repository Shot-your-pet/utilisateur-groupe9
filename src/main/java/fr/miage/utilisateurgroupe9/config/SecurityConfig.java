package fr.miage.utilisateurgroupe9.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${spring.allowed.cors}")
    private String allowedCors;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(c -> c.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/utilisateurs/**").authenticated()
                .anyRequest().denyAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Autoriser l'origine souhaitée, par exemple l'application front-end sur localhost:3000
        System.out.println("Allowed CORS: " + allowedCors);
        configuration.setAllowedOrigins(List.of(allowedCors));
        // Autoriser les méthodes HTTP que vous utilisez
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Autoriser tous les headers
        configuration.setAllowedHeaders(List.of("*"));
        // Vous pouvez aussi autoriser les credentials si besoin
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
