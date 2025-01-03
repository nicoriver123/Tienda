/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Service.Auth;

import com.example.Tienda.Models.Usuario;
import com.example.Tienda.Repo.UserRepo;
import com.example.Tienda.Util.JwtUtils;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de autenticación. Este servicio maneja las
 * operaciones de login, verificación de token y registro de usuarios. Cada
 * método interactúa con la base de datos y utiliza JWT para gestionar los
 * tokens de seguridad.
 *
 * @author USUARIO
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;// Se encarga de autenticar al usuario con su nombre de usuario y contraseña.
    @Autowired
    private PasswordEncoder passwordEncoder; // Codifica las contraseñas antes de guardarlas en la base de datos.
    @Autowired
    private UserRepo userRepo; // Repositorio que interactúa con la base de datos para las operaciones relacionadas con el usuario.

    /**
     * Método que permite realizar el login de un usuario. Autentica al usuario
     * utilizando el nombre de usuario y la contraseña, y devuelve un token JWT.
     *
     * @param username El nombre de usuario que se quiere autenticar.
     * @param password La contraseña asociada al nombre de usuario.
     * @return El token JWT generado.
     */
    @Override
    public String login(String username, String password) {
        // Crea un token de autenticación con el nombre de usuario y la contraseña.
        var authToken = new UsernamePasswordAuthenticationToken(username, password);

        // Autentica al usuario utilizando el AuthenticationManager.
        var authenticate = authenticationManager.authenticate(authToken);

        // Genera y devuelve un token JWT utilizando el nombre de usuario autenticado.
        return JwtUtils.generateToken(((UserDetails) (authenticate.getPrincipal())).getUsername());
    }

    /**
     * Método que verifica la validez de un token JWT. Extrae el nombre de
     * usuario del token y lo devuelve si el token es válido.
     *
     * @param token El token JWT que se desea verificar.
     * @return El nombre de usuario extraído del token si es válido.
     * @throws RuntimeException Si el token no es válido.
     */
    @Override
    public String verifyToken(String token) {
        // Obtiene el nombre de usuario del token.
        var usernameOptional = JwtUtils.getUsernameFromToken(token);
        // Si el token es válido, devuelve el nombre de usuario.
        if (usernameOptional.isPresent()) {
            return usernameOptional.get();
        }
        // Si el token no es válido, lanza una excepción.
        throw new RuntimeException("Token invalid");
    }

    /**
     * Método que maneja el registro de un nuevo usuario. Verifica que el nombre
     * de usuario y el correo no estén registrados previamente. Cifra la
     * contraseña y guarda al usuario en la base de datos.
     *
     * @param nombre El nombre del nuevo usuario.
     * @param username El nombre de usuario que el usuario desea utilizar.
     * @param password La contraseña del nuevo usuario.
     * @param email El correo electrónico del nuevo usuario.
     * @return El token JWT generado para el usuario recién registrado.
     * @throws RuntimeException Si el nombre de usuario o el correo electrónico
     * ya están en uso.
     */
    @Override
    public String signUp(String nombre, String username, String password, String email) {

        // Verifica si el nombre de usuario ya existe en la base de datos.
        if (userRepo.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");  // Lanza una excepción si el nombre de usuario ya existe.
        }

        // Verificar si el email ya existe
        if (userRepo.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        // Crear un nuevo objeto Usuario
        Usuario user = new Usuario();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // La contraseña se cifra antes de guardarse
        user.setNombre(nombre);
        user.setEmail(email);  // Asignar el email

        // Establecer la fecha y hora de registro
        user.setFechaRegistro(LocalDateTime.now());

        // Guardar el usuario en la base de datos
        userRepo.save(user);
        System.out.println("Usuario guardado: " + user.getUsername());

        // Generar y devolver el token JWT
        return JwtUtils.generateToken(username);
    }

}
