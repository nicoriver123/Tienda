/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Service.Auth;

import com.example.Tienda.Models.Usuario;
import com.example.Tienda.Repo.UserRepo;
import com.example.Tienda.Util.JwtUtils;
import com.example.Tienda.listener.RegistrationCompleteEvent;
import com.example.Tienda.token.VerificationToken;
import com.example.Tienda.token.VerificationTokenRepository;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

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

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
            throw new RuntimeException("El Username ya existe");  // Lanza una excepción si el nombre de usuario ya existe.
        }
        // Verificar si el email ya existe
        if (userRepo.existsByEmail(email)) {
            throw new RuntimeException("El correo electrónico ya existe");
        }
        // Crear un nuevo objeto Usuario
        Usuario user = new Usuario();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // La contraseña se cifra antes de guardarse
        user.setNombre(nombre);
        user.setEmail(email);  // Asignar el email

        // Establecer la fecha y hora de registro
        user.setFechaRegistro(LocalDateTime.now());
        user.setEnabled(false); // Asegurarnos que empiece deshabilitado

        user = userRepo.save(user);

        // Aquí deberías publicar el evento de registro
        eventPublisher.publishEvent(new RegistrationCompleteEvent(user, "..."));

        return "Verification email sent"; // No devolver JWT hasta verificación
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        // Busca un usuario en la base de datos utilizando su correo electrónico.
        // Retorna un Optional que contiene al usuario si existe, o vacío si no se encuentra.
        return userRepo.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(Usuario theUser, String token) {
        // Crea un nuevo token de verificación para el usuario proporcionado.
        // Se utiliza una clase llamada VerificationToken para almacenar el token y el usuario asociado.
        var verificationToken = new VerificationToken(token, theUser);
        // Guarda el token de verificación en el repositorio correspondiente.
        tokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String theToken) {
        // Busca el token de verificación en el repositorio utilizando su valor.
        VerificationToken token = tokenRepository.findByToken(theToken);
        if (token == null) {
            // Si el token no se encuentra, imprime un mensaje en la consola y retorna un mensaje de error.
            System.out.println("Token no encontrado en la base de datos.");
            return "Token de verificación no válido";
        }

        // Obtiene el usuario asociado al token.
        Usuario user = token.getUser();
        System.out.println("Estado actual del usuario: " + user.isEnabled());

        // Verifica si el token ha expirado comparando la fecha de expiración con la fecha actual.
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            // Si el token ha expirado, lo elimina del repositorio y retorna un mensaje de expiración.
            tokenRepository.delete(token);
            System.out.println("Token expirado y eliminado.");
            return "expired";
        }

        // Si el token es válido, habilita al usuario cambiando su estado a activo.
        user.setEnabled(true);
        try {
            // Guarda los cambios en el usuario en el repositorio.
            userRepo.save(user);
            System.out.println("Usuario actualizado. Nuevo estado: " + user.isEnabled());
            // Elimina el token usado después de completar la verificación.
            tokenRepository.delete(token);
            return "valido";
        } catch (Exception e) {
            // Si ocurre un error al guardar el usuario, imprime el error en la consola y retorna un mensaje de error.
            System.out.println("Error al guardar usuario: " + e.getMessage());
            e.printStackTrace();
            return "Error al actualizar usuario";
        }
    }

}
