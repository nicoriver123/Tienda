/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author USUARIO
 */
@Configuration // Anotación que marca esta clase como una clase de configuración en Spring.
public class SecurityFilterChainConfig {

    private final AuthenticationEntryPoint authenticationEntryPoint; // Punto de entrada para manejar excepciones de autenticación.
    private final JWTAuthenticationFilter jwtAuthenticationFilter; // Filtro personalizado para manejar la autenticación con JWT.

    // Constructor de la clase que inicializa el `authenticationEntryPoint`.
    public SecurityFilterChainConfig(AuthenticationEntryPoint authenticationEntryPoint, JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Definición de un bean que configura la seguridad para las solicitudes HTTP.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // Configuración de seguridad para solicitudes HTTP.
        // 1. Deshabilita la configuración de CORS (Cross-Origin Resource Sharing). 
        // Esto significa que no se aplicarán restricciones para solicitudes desde otros orígenes.
        httpSecurity.cors(corsConfig -> corsConfig.disable());

        // 2. Deshabilita la protección CSRF (Cross-Site Request Forgery), útil para APIs RESTful.
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // 3. Configura las reglas de autorización para las solicitudes HTTP.
        httpSecurity.authorizeHttpRequests(
                requestMatcher -> requestMatcher
                        .requestMatchers("/api/auth/login/**").permitAll() // Permite el acceso público a las rutas de login.
                        .requestMatchers("/api/auth/registrar/**").permitAll()// Permite el acceso público a las rutas de registro.
                        .requestMatchers("/api/auth/verifyEmail/**").permitAll()
                        .requestMatchers("/api/productos/**").permitAll()
                        .requestMatchers("/api/productos/crear/**").permitAll()
                        .requestMatchers("/api/productos/actualizar/**").permitAll()
                        .requestMatchers("/api/uploads/**").permitAll()
                        .anyRequest().authenticated() // Requiere autenticación para cualquier otra solicitud.
        );

        // 4. Configura cómo manejar excepciones de autenticación.
        httpSecurity.exceptionHandling(
                exceptionConfig -> exceptionConfig.authenticationEntryPoint(authenticationEntryPoint)
        // Utiliza el punto de entrada personalizado para manejar errores de autenticación.
        );

        // 5. Configura la gestión de sesiones.
        httpSecurity.sessionManagement(
                sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // Configura la política de sesiones como STATELESS, lo que significa que no se utilizarán sesiones de usuario (ideal para APIs con JWT).
        );

        // 6. Agrega un filtro personalizado antes del filtro estándar de autenticación de usuario y contraseña.
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Devuelve la configuración construida.
        return httpSecurity.build();
    }
}


