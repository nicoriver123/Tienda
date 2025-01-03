/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Controller;

import com.example.Tienda.Service.Auth.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author USUARIO
 */
@RestController // Indica que esta clase es un controlador en una aplicación REST y que los métodos devuelven directamente los datos al cliente (en formato JSON, por ejemplo).
@RequestMapping("/api/auth") // Define la ruta base para los endpoints de este controlador: "/api/auth".
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService; // Inyección de dependencia del servicio encargado de la lógica de autenticación.

    /**
     * Endpoint para el inicio de sesión (login).
     *
     * @param authRequestDto Contiene el nombre de usuario y la contraseña
     * proporcionados por el cliente.
     * @return Respuesta HTTP con un token JWT y el estado de autenticación.
     */
    @PostMapping("/login") // Define que este método manejará solicitudes POST a "/api/auth/login".
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
        // Llama al servicio para autenticar al usuario y generar un token JWT.
        var jwtToken = authService.login(authRequestDto.username(), authRequestDto.password());

        // Crea un objeto de respuesta con el token y el estado de éxito.
        var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS);

        // Devuelve una respuesta HTTP 200 (OK) con el objeto de respuesta.
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authResponseDto);
    }

    /**
     * Endpoint para registrar un nuevo usuario (sign-up).
     *
     * @param authRequestDto Contiene el nombre, nombre de usuario y contraseña
     * del nuevo usuario.
     * @return Respuesta HTTP con un token JWT si el registro fue exitoso, o un
     * mensaje de error si no.
     */
    @PostMapping("/registrar") // Define que este método manejará solicitudes POST 
    public ResponseEntity<AuthResponseDto> signUp(@RequestBody AuthRequestDto authRequestDto) {
        try {
            // Llama al servicio para registrar al usuario y generar un token JWT.
            var jwtToken = authService.signUp(authRequestDto.nombre(), authRequestDto.username(), authRequestDto.password(), authRequestDto.email());

            // Crea un objeto de respuesta con el token y el estado de éxito.
            var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.USER_CREATED_SUCCESSFULLY);

            // Devuelve una respuesta HTTP 200 (OK) con el objeto de respuesta.
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);

        } catch (Exception e) {
            // Imprime la excepción para depurar
            e.printStackTrace();

            // Devuelve un mensaje más claro sobre el error y el estado correspondiente
            var authResponseDto = new AuthResponseDto(null, AuthStatus.USER_NOT_CREATED);

            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // Cambia el estado de la respuesta a 409
                    .body(authResponseDto);
        }
    }

}
