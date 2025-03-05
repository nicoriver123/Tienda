/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Controller;

import com.example.Tienda.Models.Usuario;
import com.example.Tienda.Service.Auth.UsuarioService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author USUARIO
 */
/**
 * Controlador REST para manejar las operaciones relacionadas con los usuarios.
 * Este controlador expone endpoints para obtener y actualizar el perfil del usuario,
 * así como para actualizar la imagen de perfil.
 */
@RestController // Indica que esta clase es un controlador REST.
@RequestMapping("/api/usuario") // Define la ruta base para todos los endpoints en este controlador.
public class UsuarioController {

    @Autowired // Inyecta automáticamente una instancia de UsuarioService.
    private UsuarioService usuarioService;

    /**
     * Endpoint para obtener el perfil del usuario autenticado.
     *
     * @param userDetails Detalles del usuario autenticado (proporcionado por Spring Security).
     * @return ResponseEntity con los datos del usuario.
     */
    @GetMapping("/perfil")
    public ResponseEntity<Usuario> getPerfilUsuario(@AuthenticationPrincipal UserDetails userDetails) {
        // Obtener el nombre de usuario del usuario autenticado.
        String username = userDetails.getUsername();

        // Obtener los datos del usuario desde el servicio.
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);

        // Devolver los datos del usuario con un código de estado 200 OK.
        return ResponseEntity.ok(usuario);
    }

    /**
     * Endpoint para actualizar el perfil del usuario.
     *
     * @param userDetails Detalles del usuario autenticado.
     * @param usuarioActualizado Objeto Usuario con los nuevos datos.
     * @return ResponseEntity con el usuario actualizado o un mensaje de error.
     */
    @PutMapping("/perfil/actualizar")
    public ResponseEntity<?> actualizarPerfilUsuario(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Usuario usuarioActualizado) {

        try {
            // Obtener el nombre de usuario del usuario autenticado.
            String username = userDetails.getUsername();

            // Obtener el usuario actual desde la base de datos.
            Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);

            // Verificar si el usuario existe.
            if (usuario == null) {
                // Si el usuario no existe, devolver un error 404 Not Found.
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }

            // Validar campos obligatorios.
            if (usuarioActualizado.getNombre() == null || usuarioActualizado.getNombre().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre es obligatorio");
            }
            if (usuarioActualizado.getEmail() == null || usuarioActualizado.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("El email es obligatorio");
            }

            // Actualizar los campos permitidos.
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setEmail(usuarioActualizado.getEmail());
            usuario.setTelefono(usuarioActualizado.getTelefono());
            usuario.setPais(usuarioActualizado.getPais());

            // Guardar los cambios en la base de datos.
            Usuario usuarioActualizadoDB = usuarioService.actualizarUsuario(usuario);

            // Devolver el usuario actualizado con un código de estado 200 OK.
            return ResponseEntity.ok(usuarioActualizadoDB);

        } catch (Exception e) {
            // Manejar cualquier error inesperado y devolver un error 500 Internal Server Error.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el perfil: " + e.getMessage());
        }
    }

    /**
     * Endpoint para actualizar la imagen de perfil del usuario.
     *
     * @param userDetails Detalles del usuario autenticado.
     * @param imagen Archivo de imagen enviado por el usuario.
     * @return ResponseEntity con el usuario actualizado o un error.
     */
    @PutMapping("/perfil/actualizar/imagen")
    public ResponseEntity<Usuario> actualizarImagenPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("imagen") MultipartFile imagen) {

        // Obtener el nombre de usuario del usuario autenticado.
        String username = userDetails.getUsername();

        // Obtener el usuario actual desde la base de datos.
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(username);

        try {
            // Subir la imagen y obtener la URL.
            String imageUrl = uploadImage(imagen);

            // Actualizar la URL de la imagen en el perfil del usuario.
            usuario.setImagenUrl(imageUrl);

            // Guardar los cambios en la base de datos.
            Usuario usuarioActualizadoDB = usuarioService.actualizarUsuario(usuario);

            // Devolver el usuario actualizado con un código de estado 200 OK.
            return ResponseEntity.ok(usuarioActualizadoDB);
        } catch (IOException e) {
            // Si ocurre un error al subir la imagen, devolver un error 500 Internal Server Error.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Método privado para subir una imagen al servidor y generar una URL.
     *
     * @param file Archivo de imagen.
     * @return URL de la imagen subida.
     * @throws IOException Si ocurre un error al subir la imagen.
     */
    private String uploadImage(MultipartFile file) throws IOException {
        // Definir la ruta donde se guardarán las imágenes.
        Path uploadPath = Paths.get("uploads/");

        // Crear el directorio si no existe.
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Obtener el nombre original del archivo.
        String originalFilename = file.getOriginalFilename();

        // Generar un nombre único para el archivo usando UUID.
        String fileName = UUID.randomUUID().toString();

        // Obtener la extensión del archivo original.
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        } else {
            extension = ".jpg"; // Si no tiene extensión, usar .jpg por defecto.
        }
        fileName += extension; // Concatenar la extensión al nombre único.

        // Crear la ruta completa del archivo.
        Path filePath = uploadPath.resolve(fileName);

        // Copiar el archivo al servidor.
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Devolver la URL de la imagen subida.
        return "/api/uploads/" + fileName;
    }
}