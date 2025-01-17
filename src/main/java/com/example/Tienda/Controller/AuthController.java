/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Controller;

import com.example.Tienda.Service.Auth.AuthService;
import com.example.Tienda.Service.Auth.AuthServiceImpl;
import com.example.Tienda.token.VerificationToken;
import com.example.Tienda.token.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author USUARIO
 */
@RestController // Indica que esta clase es un controlador en una aplicación REST y que los métodos devuelven directamente los datos al cliente (en formato JSON, por ejemplo).
@RequestMapping("/api/auth") // Define la ruta base para los endpoints de este controlador: "/api/auth".
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService; // Inyección de dependencia del servicio encargado de la lógica de autenticación.
    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private AuthServiceImpl userService;

    /**
     * Endpoint para el inicio de sesión (login).
     *
     * @param authRequestDto Contiene el nombre de usuario y la contraseña
     * proporcionados por el cliente.
     * @return Respuesta HTTP con un token JWT y el estado de autenticación.
     */
    @PostMapping("/login") // Define que este método manejará solicitudes POST a "/api/auth/login".
public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
    try {
            // Llama al servicio para autenticar al usuario y generar un token JWT
        var jwtToken = authService.login(authRequestDto.username(), authRequestDto.password());

            // Crea un objeto de respuesta con el token y el estado de éxito
        var authResponseDto = new AuthResponseDto(
                jwtToken,
                AuthStatus.LOGIN_SUCCESS,
                "Inicio de sesión exitoso"
        );

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);

    } catch (Exception e) {
        String errorMessage = e.getMessage();
        AuthStatus status = AuthStatus.LOGIN_FAILED;

        if (errorMessage.contains("Usuario no encontrado")) {
            errorMessage = "Usuario no encontrado";
        } else if (errorMessage.contains("La cuenta no ha sido verificada")) {
            errorMessage = "La cuenta no ha sido verificada. Por favor, revise su correo electrónico.";
        } else if (errorMessage.contains("Bad credentials")) {
            errorMessage = "Usuario o contraseña incorrectos";
        }

        var authResponseDto = new AuthResponseDto(null, status, errorMessage);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponseDto);
    }
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
            var authResponseDto = new AuthResponseDto(jwtToken,
                    AuthStatus.USER_CREATED_SUCCESSFULLY,
                    "Usuario registrado exitosamente");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            AuthStatus status = AuthStatus.USER_NOT_CREATED;

            // Personalizar mensajes según el tipo de error
            if (e.getMessage().contains("Username already exists")) {
                errorMessage = "El nombre de usuario ya está en uso";
            } else if (e.getMessage().contains("Email already exists")) {
                errorMessage = "El correo electrónico ya está registrado";
            }

            var authResponseDto = new AuthResponseDto(null, status, errorMessage);

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(authResponseDto);
        }
    }

    @GetMapping("/verifyEmail")
    public void verifyEmail(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        // Imprime el token recibido en la consola para verificación
        System.out.println("Recibida solicitud de verificación para token: " + token);

        try {
            // Busca el token de verificación en la base de datos utilizando el repositorio
            VerificationToken theToken = tokenRepository.findByToken(token);

            // Si no se encuentra el token, redirige al usuario a la página de verificación con un mensaje de error
            if (theToken == null) {
                System.out.println("Token no encontrado");
                response.sendRedirect("http://localhost:3000/verification?status=invalid-token");
                return;
            }

            // Valida el token y guarda el resultado
            String result = userService.validateToken(token);

            // Imprime el resultado de la validación en la consola para saber si es válido, expirado, etc.
            System.out.println("Resultado de la validación: " + result);

            // Dependiendo del resultado de la validación, redirige al usuario a diferentes páginas
            switch (result) {
                case "valido":
                    // Si el token es válido, redirige con un mensaje de éxito
                    response.sendRedirect("http://localhost:3000/verification?status=success");
                    break;
                case "expired":
                    // Si el token ya ha expirado, redirige con un mensaje de expiración
                    response.sendRedirect("http://localhost:3000/verification?status=expired");
                    break;
                default:
                    // Si el token no es válido, redirige con un mensaje de error
                    response.sendRedirect("http://localhost:3000/verification?status=invalid-token");
            }
        } catch (Exception e) {
            // Si ocurre algún error durante el proceso de verificación, se captura la excepción
            // Se imprime el mensaje del error en la consola y se redirige al usuario a una página de error
            System.out.println("Error durante la verificación: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("http://localhost:3000/verification?status=error");
        }
    }

    public String applicationUrl(HttpServletRequest request) {
        // Construye la URL completa del servidor (incluye el nombre del servidor, puerto y contexto) para usarla en la verificación por correo
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
